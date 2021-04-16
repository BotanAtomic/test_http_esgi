package io.deepn.exchange.exchange.binance.event

import io.deepn.common.TimeFrame
import io.deepn.exchange.exchange.binance.entity.ws.ListenKey

enum class SubscriptionMethod {
    SUBSCRIBE,
    UNSUBSCRIBE
}

typealias Stream = String

data class BinanceWebSocketRequest(
    val method: SubscriptionMethod,
    val params: List<Stream>,
    val id: Long,
    @Transient var accepted: Boolean = false,
    @Transient var pending: Boolean = false
) {
    fun accept(): BinanceWebSocketRequest {
        this.accepted = true
        this.pending = false
        return this
    }
}

fun String.coinStream(stream: Stream) = "${this.toLowerCase()}@${stream}"

fun aggregateTradeStream(symbol: String) = symbol.coinStream("aggTrade")

fun tradeStream(symbol: String) = symbol.coinStream("trade")

fun klineStream(symbol: String, timeFrame: TimeFrame) = symbol.coinStream("kline_${timeFrame.code}")

fun miniTickerStream(symbol: String) = symbol.coinStream("miniTicker")

fun miniTickerStream() = "!miniTicker@arr"

fun tickerStream(symbol: String) = symbol.coinStream("ticker")

fun tickerStream() = "!ticker@arr"

fun bookTickerStream(symbol: String) = symbol.coinStream("bookTicker")

fun bookTickerStream() = "!bookTicker"

fun accountStream(key: ListenKey) = key.listenKey





