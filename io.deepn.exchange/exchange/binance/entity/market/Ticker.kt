package io.deepn.exchange.exchange.binance.entity.market

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.Instant

data class Ticker(
    val symbol: String,
    val priceChange: BigDecimal,
    val priceChangePercent: BigDecimal,
    @SerializedName("weightedAvgPrice") val weightedAveragePrice: BigDecimal,
    @SerializedName("prevClosePrice") val previousClosePrice: BigDecimal,
    val lastPrice: BigDecimal,
    @SerializedName("lastQty") val lastQuantity: BigDecimal,
    val bidPrice: BigDecimal,
    val askPrice: BigDecimal,
    val openPrice: BigDecimal,
    val highPrice: BigDecimal,
    val lowPrice: BigDecimal,
    val volume: BigDecimal,
    val quoteVolume: BigDecimal,
    val openTime: Instant,
    val closeTime: Instant,
    val firstId: Long,
    val lastId: Long,
    val count: Long
)

data class TickerPrice(
    val symbol: String,
    val price: BigDecimal
)

data class BookTicker(
    val symbol: String,
    val bidPrice: BigDecimal,
    @SerializedName("bidQty") val bidQuantity: BigDecimal,
    val askPrice: BigDecimal,
    @SerializedName("askQty") val askQuantity: BigDecimal
)