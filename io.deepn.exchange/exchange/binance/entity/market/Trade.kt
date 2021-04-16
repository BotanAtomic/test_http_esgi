package io.deepn.exchange.exchange.binance.entity.market

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class AccountTrade(
    val symbol: String,
    val id: Long,
    val orderId: Long,
    val orderListId: Long,
    val price: BigDecimal,
    @SerializedName("qty") val quantity: BigDecimal,
    @SerializedName("quoteQty") val quoteQuantity: BigDecimal,
    val commission: BigDecimal,
    val commissionAsset: String,
    val time: Instant,
    val isBuyer: Boolean,
    val isMaker: Boolean,
    val isBestMatch: Boolean
)

data class TradeHistoryItem(
    val id: Long,
    val price: BigDecimal,
    @SerializedName("qty") val quantity: BigDecimal,
    @SerializedName("quoteQty") val quoteQuantity: BigDecimal,
    val time: Instant,
    val isBuyerMaker: Boolean,
    val isBestMatch: Boolean
)

data class AverageTrade(
    val mins: Int,
    val price: BigDecimal
)

data class AggregateTrade(
    @SerializedName("s") val symbol: String,
    @SerializedName("a") val id: Long,
    @SerializedName("p") val price: BigDecimal,
    @SerializedName("q") val quantity: BigDecimal,
    @SerializedName("f") val firstTradeId: Long,
    @SerializedName("l") val lastTradeId: Long,
    @SerializedName("T") val time: Instant,
    @SerializedName("m") val isBuyerMaker: Boolean,
    @SerializedName("M") val isBestMatch: Boolean
)

data class Trade(
    val price: BigDecimal,
    @SerializedName("qty") val quantity: BigDecimal,
    val commission: BigDecimal,
    val commissionAsset: String
)
