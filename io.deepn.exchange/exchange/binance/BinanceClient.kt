package io.deepn.exchange.exchange.binance

import io.deepn.common.OrderSide
import io.deepn.common.OrderType
import io.deepn.common.TimeFrame
import io.deepn.exchange.ExchangeClient
import io.deepn.exchange.exchange.binance.constant.VALID_DEPTH_LIMITS
import io.deepn.exchange.exchange.binance.entity.market.*
import io.deepn.exchange.exchange.binance.error.*
import io.deepn.exchange.exchange.binance.security.AuthenticationInterceptor
import io.deepn.exchange.serialization.UnixInstantDeserializer
import io.deepn.exchange.utils.between
import io.deepn.exchange.utils.closestValue
import io.deepn.exchange.utils.gsonWithAdapters
import io.deepn.exchange.utils.validateDates
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import reactor.core.publisher.Mono
import retrofit2.Converter
import retrofit2.Response
import java.time.Duration
import java.time.Instant
import java.util.*

data class BinanceCredentials(val apiKey: String, val secret: String)

class BinanceClient(
    private val credentials: BinanceCredentials? = null
) : ExchangeClient(
    "https://api.binance.com/api/v3/",
    gsonWithAdapters(
        Candle::class to CandleDeserializer(),
        OrderBook::class to OrderBookDeserializer(),
        Instant::class to UnixInstantDeserializer(),
    ).apply { serializeNulls() },
    OkHttpClient.Builder().apply {
        credentials?.let { addInterceptor(AuthenticationInterceptor(it)) }
    }
) {

    private val marketService = createService(MarketDataService::class)
    private val accountService = createService(AccountService::class)
    private val generalService = createService(GeneralService::class)

    private val errorConverter: Converter<ResponseBody, BinanceApiError> = serviceProvider.responseBodyConverter(
        BinanceApiError::class.java,
        emptyArray()
    )

    private fun <T> Mono<Response<T>>.wrap(authenticated: Boolean = false): Mono<T> =
        if (authenticated && credentials == null) Mono.error(
            BinanceApiError(UNAUTHORIZED).toException()
        ) else this.map {
            val response = BinanceApiResponse(errorConverter, it)
            val body = response.body
            val errorBody = response.errorBody
            if (response.isError() || body == null)
                throw errorBody?.toException() ?: BinanceApiException()

            body
        }

    fun ping() = generalService.ping().wrap()

    fun serverTime() = generalService.serverTime().wrap()

    fun exchangeInfo() = generalService.exchangeInfo().wrap()

    fun depth(symbol: String, limit: Int? = null) = marketService.depth(
        symbol,
        VALID_DEPTH_LIMITS.closestValue(limit)
    ).wrap()

    fun trades(symbol: String, limit: Int? = null) = marketService.trades(
        symbol,
        limit.between(1, 1000)
    ).wrap()

    fun historicalTrades(
        symbol: String,
        limit: Int? = null,
        fromId: Long? = null
    ) = marketService.historicalTrades(
        symbol,
        limit.between(1, 1000), fromId
    ).wrap(true)

    fun aggregateTrades(
        symbol: String, limit: Int? = null,
        fromId: Long? = null, startTime: Instant? = null,
        endTime: Instant? = null
    ): Mono<List<AggregateTrade>> {
        validateDates(
            startTime, endTime, Duration.ofMinutes(60),
            MORE_THAN_XX_HOURS.toBinanceException()
        )?.let { return@let it }
        return marketService.aggregateTrades(
            symbol, limit.between(1, 1000),
            fromId, startTime?.toEpochMilli(), endTime?.toEpochMilli()
        ).wrap()
    }

    fun candles(
        symbol: String, interval: TimeFrame,
        limit: Int? = null, startTime: Instant? = null,
        endTime: Instant? = null
    ) = marketService.candles(
        symbol, limit.between(1, 1000),
        interval.code, startTime?.toEpochMilli(),
        endTime?.toEpochMilli()
    ).wrap()

    fun averagePrice(symbol: String) = marketService.averagePrice(symbol).wrap()

    fun ticker24h(symbol: String) = marketService.ticker24h(symbol).wrap()

    fun ticker24h() = marketService.ticker24h().wrap()

    fun tickerPrice(symbol: String) = marketService.tickerPrice(symbol).wrap()

    fun tickerPrice() = marketService.tickerPrice().wrap()

    fun bookTicker(symbol: String) = marketService.bookTicker(symbol).wrap()

    fun bookTicker() = marketService.bookTicker().wrap()

    fun newLimitOrder(
        symbol: String, side: OrderSide,
        timeInForce: TimeInForce = TimeInForce.GTC,
        quantity: String, price: String,
        responseType: OrderResponseType = OrderResponseType.FULL,
    ) = accountService.order(
        symbol = symbol, side = side,
        type = OrderType.LIMIT, timeInForce = timeInForce,
        quantity = quantity, price = price,
        responseType = responseType
    ).wrap(true)

    fun newMarketOrder(
        symbol: String, side: OrderSide,
        quantity: String, responseType: OrderResponseType = OrderResponseType.FULL,
    ) = accountService.order(
        symbol = symbol, side = side,
        type = OrderType.MARKET, quantity = quantity,
        responseType = responseType
    ).wrap(true)

    fun newStopLossOrder(
        symbol: String, side: OrderSide,
        quantity: String, stopPrice: String,
        responseType: OrderResponseType = OrderResponseType.FULL,
    ) = accountService.order(
        symbol = symbol, side = side,
        type = OrderType.STOP_LOSS, quantity = quantity,
        stopPrice = stopPrice, responseType = responseType
    ).wrap(true)

    fun newTakeProfitOrder(
        symbol: String, side: OrderSide,
        quantity: String, stopPrice: String,
        responseType: OrderResponseType = OrderResponseType.FULL,
    ) = accountService.order(
        symbol = symbol, side = side,
        type = OrderType.TAKE_PROFIT, quantity = quantity,
        stopPrice = stopPrice, responseType = responseType
    ).wrap(true)

    fun order(symbol: String, orderId: Long) = accountService.order(symbol, orderId)
        .wrap(true)

    fun cancelOrder(symbol: String, orderId: Long) = accountService.cancelOrder(symbol, orderId)
        .wrap(true)

    fun cancelOpenOrders(symbol: String) = accountService.cancelOpenOrders(symbol)
        .wrap(true)

    fun openOrders(symbol: String? = null) = accountService.getOpenOrders(symbol)
        .wrap(true)

    fun allOrders(
        symbol: String, orderId: Long? = null,
        startTime: Instant? = null, endTime: Instant? = null
    ): Mono<List<Order>> {
        return accountService.getAllOrders(
            symbol, orderId,
            startTime?.toEpochMilli(), endTime?.toEpochMilli()
        ).wrap(true)
    }

    fun getAccount() = accountService.getAccount().wrap(true)

    fun getAccountTrades(symbol: String) = accountService.getAccountTrades(symbol)
        .wrap(true)

    fun createUserStream() = accountService.createUserStream().wrap(true)

    fun keepAliveUserStream(listenKey: String) = accountService.keepAliveUserStream(listenKey)
        .wrap(true)

    fun closeUserStream(listenKey: String) = accountService.closeUserStream(listenKey)
        .wrap(true)

}
