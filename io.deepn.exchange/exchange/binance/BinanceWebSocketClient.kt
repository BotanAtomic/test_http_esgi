package io.deepn.exchange.exchange.binance

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.deepn.exchange.ExchangeWebSocketClient
import io.deepn.common.TimeFrame
import io.deepn.exchange.exchange.binance.constant.MAX_STREAM_SIZE
import io.deepn.exchange.exchange.binance.entity.ws.*
import io.deepn.exchange.exchange.binance.event.*
import io.deepn.exchange.serialization.UnixInstantDeserializer
import io.deepn.exchange.utils.directMulticastBestEffort
import io.deepn.exchange.utils.getLong
import io.deepn.exchange.utils.gsonWithAdapters
import okhttp3.Response
import okhttp3.WebSocket
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class BinanceWebSocketClient : ExchangeWebSocketClient(
    "wss://stream.binance.com:9443/ws",
    gsonWithAdapters(
        Instant::class to UnixInstantDeserializer(),
        CandleEvent::class to BinanceCandleEventDeserializer()
    )
) {

    private val subscriptionPublisher = directMulticastBestEffort<BinanceWebSocketRequest>()
    private val errorPublisher = directMulticastBestEffort<BinanceWebSocketError>()

    private val candlePublisher = directMulticastBestEffort<CandleEvent>()
    private val aggregateTradePublisher = directMulticastBestEffort<AggregateTradeEvent>()
    private val tradePublisher = directMulticastBestEffort<TradeEvent>()
    private val miniTickerPublisher = directMulticastBestEffort<MiniTicker>()
    private val tickerPublisher = directMulticastBestEffort<Ticker>()
    private val accountPositionPublisher = directMulticastBestEffort<OutboundAccountPosition>()

    private val requests = ConcurrentHashMap<Long, BinanceWebSocketRequest>(MAX_STREAM_SIZE)

    private val idGenerator = AtomicLong(0)

    init {
        connect()
        errorPublisher.asFlux().subscribe {
            onFailure(webSocket, it, null)
        }
    }

    fun restart() {
        requests.values.filter { it.accepted }.forEach { it.pending = true }
        connect()
    }

    fun close() {
        webSocket.close(1000, null)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.connected.set(true)
        requests.values.filter { it.pending }.forEach { request ->
            request.send()
            request.pending = false
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        webSocket.close(1002, null)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.connected.set(false)
        restart()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val json = gson.fromJson(text, JsonElement::class.java)

        if (json.isJsonObject) {
            json.asJsonObject.let { jsonObject ->
                when {
                    jsonObject.has("result") ->
                        requests[jsonObject.getLong("id")]?.let {
                            subscriptionPublisher.tryEmitNext(it.accept())
                            println("Accept $it")
                        }
                    jsonObject.has("error") -> errorPublisher.tryEmitNext(
                        jsonObject.getAsJsonObject("error")
                            .convert(BinanceWebSocketError::class)
                    )
                    else -> publish(jsonObject)
                }
            }
        } else if (json.isJsonArray)
            json.asJsonArray.forEach { publish(it.asJsonObject) }
    }

    private inline fun <reified T : Any> JsonObject.publish(publisher: Sinks.Many<T>) {
        publisher.tryEmitNext(this.convert(T::class.java))
    }

    private fun publish(element: JsonObject) {
        element.get("e")?.asString?.let {
            when (it.toLowerCase()) {
                "kline" -> element.publish(candlePublisher)
                "aggtrade" -> element.publish(aggregateTradePublisher)
                "trade" -> element.publish(tradePublisher)
                "24hrminiticker" -> element.publish(miniTickerPublisher)
                "24hrticker" -> element.publish(tickerPublisher)
                "outboundaccountposition" -> element.publish(accountPositionPublisher)
            }
        }
    }

    private fun send(method: SubscriptionMethod, streams: List<Stream>): BinanceWebSocketRequest? {
        if (streams.size + requests.values.filter { it.accepted }.size > MAX_STREAM_SIZE) return null
        requests.values.filter { it.method == method }.find { it.params.containsAll(streams) }?.let {
            println("Already subscribed to $streams")
            return it
        }
        val request = BinanceWebSocketRequest(
            method,
            streams,
            idGenerator.incrementAndGet(),
            pending = isConnected().not()
        )

        if (request.pending.not())
            request.send()

        requests[request.id] = request
        return request
    }

    private fun subscribe(streams: List<Stream>) = send(SubscriptionMethod.SUBSCRIBE, streams)

    private fun subscribe(stream: Stream) = send(SubscriptionMethod.SUBSCRIBE, listOf(stream))

    fun unsubscribe(streams: List<Stream>) = send(SubscriptionMethod.UNSUBSCRIBE, streams)

    fun unsubscribe(stream: Stream) = send(SubscriptionMethod.UNSUBSCRIBE, listOf(stream))

    fun unsubscribe(webSocketRequest: BinanceWebSocketRequest) =
        send(SubscriptionMethod.UNSUBSCRIBE, webSocketRequest.params)

    fun onSubscribeEvent(request: BinanceWebSocketRequest? = null): Mono<BinanceWebSocketRequest> {
        if (request != null) {
            return if (request.accepted) Mono.just(request)
            else subscriptionPublisher.asFlux().filter { it.id == request.id }.next()
        }
        return subscriptionPublisher.asFlux().next()
    }

    fun subscribeToCandleEvent(symbol: String, timeFrame: TimeFrame): BinanceWebSocketRequest? {
        return subscribe(klineStream(symbol, timeFrame))
    }

    fun subscribeToCandleEvents(list: List<Pair<String, TimeFrame>>): BinanceWebSocketRequest? {
        return subscribe(list.map { klineStream(it.first, it.second) })
    }

    fun onCandleEvent(symbol: String, timeFrame: TimeFrame): Flux<CandleEvent> {
        return candlePublisher.asFlux().filter { it.symbol == symbol && it.interval == timeFrame }
    }

    fun onAggregateEvent(symbol: String): SubscriptionResponse<AggregateTradeEvent> {
        return subscribe(aggregateTradeStream(symbol))
            .toResponse(aggregateTradePublisher.asFlux().filter { it.symbol == symbol })
    }

    fun onTradeEvent(symbol: String): SubscriptionResponse<TradeEvent> {
        return subscribe(tradeStream(symbol)).toResponse(
            tradePublisher.asFlux().filter { it.symbol == symbol }
        )
    }

    fun onMiniTicker(symbol: String): SubscriptionResponse<MiniTicker> {
        return subscribe(miniTickerStream(symbol)).toResponse(
            miniTickerPublisher.asFlux().filter { it.symbol == symbol }
        )
    }

    fun onAllMiniTicker(): SubscriptionResponse<MiniTicker> {
        return subscribe(miniTickerStream()).toResponse(
            miniTickerPublisher.asFlux()
        )
    }

    fun onTicker(symbol: String): SubscriptionResponse<Ticker> {
        return subscribe(tickerStream(symbol)).toResponse(
            tickerPublisher.asFlux().filter { it.symbol == symbol }
        )
    }

    fun onAllTicker(): SubscriptionResponse<Ticker> {
        return subscribe(tickerStream()).toResponse(
            tickerPublisher.asFlux()
        )
    }

    fun onAccountPosition(): Flux<OutboundAccountPosition> {
        return accountPositionPublisher.asFlux()
    }

    fun subscribeToAccountEvents(listenKey: ListenKey) = subscribe(accountStream(listenKey))
}

data class SubscriptionResponse<T : Any>(val request: BinanceWebSocketRequest?, val flux: Flux<T>)

fun <T : Any> BinanceWebSocketRequest?.toResponse(flux: Flux<T>) = SubscriptionResponse(this, flux)
