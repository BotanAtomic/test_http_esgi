package io.deepn.exchange.exchange.binance

import io.deepn.common.OrderSide
import io.deepn.common.OrderType
import io.deepn.exchange.exchange.binance.constant.DEFAULT_RECEIVING_WINDOW
import io.deepn.exchange.exchange.binance.constant.KEY_HEADER
import io.deepn.exchange.exchange.binance.constant.SIGNED_HEADER
import io.deepn.exchange.exchange.binance.entity.account.Account
import io.deepn.exchange.exchange.binance.entity.general.ExchangeInfo
import io.deepn.exchange.exchange.binance.entity.general.ServerTime
import io.deepn.exchange.exchange.binance.entity.market.*
import io.deepn.exchange.exchange.binance.entity.ws.ListenKey
import reactor.core.publisher.Mono
import retrofit2.Response
import retrofit2.http.*
import java.time.Instant

interface GeneralService {

    @GET("ping")
    fun ping(): Mono<Response<Any>>

    @GET("time")
    fun serverTime(): Mono<Response<ServerTime>>

    @GET("exchangeInfo")
    fun exchangeInfo(): Mono<Response<ExchangeInfo>>
}

interface MarketDataService {

    @GET("depth")
    fun depth(
        @Query("symbol") symbol: String,
        @Query("limit") limit: Int?
    ): Mono<Response<OrderBook>>

    @GET("trades")
    fun trades(
        @Query("symbol") symbol: String,
        @Query("limit") limit: Int?
    ): Mono<Response<List<TradeHistoryItem>>>

    @GET("historicalTrades")
    @Headers(KEY_HEADER)
    fun historicalTrades(
        @Query("symbol") symbol: String,
        @Query("limit") limit: Int?,
        @Query("fromId") fromId: Long?
    ): Mono<Response<List<TradeHistoryItem>>>

    @GET("aggTrades")
    fun aggregateTrades(
        @Query("symbol") symbol: String,
        @Query("limit") limit: Int?,
        @Query("fromId") fromId: Long?,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?
    ): Mono<Response<List<AggregateTrade>>>

    @GET("klines")
    fun candles(
        @Query("symbol") symbol: String,
        @Query("limit") limit: Int?,
        @Query("interval") interval: String,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?
    ): Mono<Response<List<Candle>>>

    @GET("avgPrice")
    fun averagePrice(@Query("symbol") symbol: String): Mono<Response<AverageTrade>>

    @GET("ticker/24hr")
    fun ticker24h(@Query("symbol") symbol: String): Mono<Response<Ticker>>

    @GET("ticker/24hr")
    fun ticker24h(): Mono<Response<List<Ticker>>>

    @GET("ticker/price")
    fun tickerPrice(@Query("symbol") symbol: String): Mono<Response<TickerPrice>>

    @GET("ticker/price")
    fun tickerPrice(): Mono<Response<List<TickerPrice>>>

    @GET("ticker/bookTicker")
    fun bookTicker(@Query("symbol") symbol: String): Mono<Response<BookTicker>>

    @GET("ticker/bookTicker")
    fun bookTicker(): Mono<Response<List<BookTicker>>>
}

interface AccountService {

    @POST("order")
    @Headers(KEY_HEADER, SIGNED_HEADER)
    fun order(
        @Query("symbol") symbol: String,
        @Query("side") side: OrderSide,
        @Query("type") type: OrderType,
        @Query("timeInForce") timeInForce: TimeInForce? = null,
        @Query("quantity") quantity: String? = null,
        @Query("quoteOrderQty") quoteQuantity: String? = null,
        @Query("price") price: String? = null,
        @Query("newClientOrderId") newClientOrderId: String? = null,
        @Query("stopPrice") stopPrice: String? = null,
        @Query("icebergQty") icebergQuantity: String? = null,
        @Query("newOrderRespType") responseType: OrderResponseType = OrderResponseType.FULL,
        @Query("recvWindow") receiveWindows: Long? = DEFAULT_RECEIVING_WINDOW,
        @Query("timestamp") timestamp: Long = Instant.now().toEpochMilli(),
    ): Mono<Response<NewOrder>>

    @GET("order")
    @Headers(KEY_HEADER, SIGNED_HEADER)
    fun order(
        @Query("symbol") symbol: String,
        @Query("orderId") orderId: Long? = null,
        @Query("origClientOrderId") originalClientOrderId: String? = null,
        @Query("recvWindow") receiveWindows: Long? = DEFAULT_RECEIVING_WINDOW,
        @Query("timestamp") timestamp: Long = Instant.now().toEpochMilli()
    ): Mono<Response<Order>>

    @DELETE("order")
    @Headers(KEY_HEADER, SIGNED_HEADER)
    fun cancelOrder(
        @Query("symbol") symbol: String,
        @Query("orderId") orderId: Long? = null,
        @Query("origClientOrderId") originalClientOrderId: String? = null,
        @Query("recvWindow") receiveWindows: Long? = DEFAULT_RECEIVING_WINDOW,
        @Query("timestamp") timestamp: Long = Instant.now().toEpochMilli()
    ): Mono<Response<CancelOrder>>


    @DELETE("openOrders")
    @Headers(KEY_HEADER, SIGNED_HEADER)
    fun cancelOpenOrders(
        @Query("symbol") symbol: String,
        @Query("recvWindow") receiveWindows: Long? = DEFAULT_RECEIVING_WINDOW,
        @Query("timestamp") timestamp: Long = Instant.now().toEpochMilli()
    ): Mono<Response<List<CancelOrder>>>

    @GET("openOrders")
    @Headers(KEY_HEADER, SIGNED_HEADER)
    fun getOpenOrders(
        @Query("symbol") symbol: String? = null,
        @Query("recvWindow") receiveWindows: Long? = DEFAULT_RECEIVING_WINDOW,
        @Query("timestamp") timestamp: Long = Instant.now().toEpochMilli()
    ): Mono<Response<List<Order>>>

    @GET("allOrders")
    @Headers(KEY_HEADER, SIGNED_HEADER)
    fun getAllOrders(
        @Query("symbol") symbol: String,
        @Query("orderId") orderId: Long? = null,
        @Query("startTime") startTime: Long?,
        @Query("endTime") endTime: Long?,
        @Query("recvWindow") receiveWindows: Long? = DEFAULT_RECEIVING_WINDOW,
        @Query("timestamp") timestamp: Long = Instant.now().toEpochMilli()
    ): Mono<Response<List<Order>>>

    @GET("account")
    @Headers(KEY_HEADER, SIGNED_HEADER)
    fun getAccount(
        @Query("recvWindow") receiveWindows: Long? = DEFAULT_RECEIVING_WINDOW,
        @Query("timestamp") timestamp: Long = Instant.now().toEpochMilli()
    ): Mono<Response<Account>>

    @GET("myTrades")
    @Headers(KEY_HEADER, SIGNED_HEADER)
    fun getAccountTrades(
        @Query("symbol") symbol: String,
        @Query("startTime") startTime: Long? = null,
        @Query("endTime") endTime: Long? = null,
        @Query("fromId") orderId: Long? = null,
        @Query("limit") limit: Int? = null,
        @Query("recvWindow") receiveWindows: Long? = DEFAULT_RECEIVING_WINDOW,
        @Query("timestamp") timestamp: Long = Instant.now().toEpochMilli()
    ): Mono<Response<List<AccountTrade>>>

    @POST("userDataStream")
    @Headers(KEY_HEADER)
    fun createUserStream(): Mono<Response<ListenKey>>

    @PUT("userDataStream")
    @Headers(KEY_HEADER)
    fun keepAliveUserStream(@Query("listenKey") listenKey: String): Mono<Response<Any>>

    @DELETE("userDataStream")
    @Headers(KEY_HEADER)
    fun closeUserStream(@Query("listenKey") listenKey: String): Mono<Response<Any>>

}