package io.deepn.exchange.exchange.binance.entity.ws

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import io.deepn.common.TimeFrame
import io.deepn.exchange.utils.*
import java.lang.reflect.Type
import java.math.BigDecimal
import java.time.Instant

data class ListenKey(val listenKey: String)

data class BinanceWebSocketError(val code: Int, @SerializedName("msg") override val message: String) : Exception(message)

interface WebSocketEvent {
    val eventTime: Instant
}

data class CandleEvent(
    override val eventTime: Instant,
    val symbol: String,
    val openTime: Instant,
    val closeTime: Instant,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: BigDecimal,
    val interval: TimeFrame,
    val firstTradeId: Long,
    val lastTradeId: Long,
    val quoteAssetVolume: BigDecimal,
    val numberOfTrades: Long,
    val takerBuyBaseAssetVolume: BigDecimal,
    val takerBuyQuoteAssetVolume: BigDecimal,
    val isBarFinal: Boolean,
) : WebSocketEvent

class BinanceCandleEventDeserializer : JsonDeserializer<CandleEvent?> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext
    ): CandleEvent {
        val data = json.asJsonObject

        val node = data.get("k").asJsonObject

        return CandleEvent(
            eventTime = data.getInstant("E"),
            symbol = data.getString("s"),
            openTime = node.getInstant("t"),
            closeTime = node.getInstant("T"),
            open = node.getBigDecimal("o"),
            close = node.getBigDecimal("c"),
            high = node.getBigDecimal("h"),
            low = node.getBigDecimal("l"),
            volume = node.getBigDecimal("v"),
            numberOfTrades = node.getLong("n"),
            isBarFinal = node.getBoolean("x"),
            interval = TimeFrame.get(node.getString("i")),
            firstTradeId = node.getLong("f"),
            lastTradeId = node.getLong("L"),
            quoteAssetVolume = node.getBigDecimal("q"),
            takerBuyBaseAssetVolume = node.getBigDecimal("V"),
            takerBuyQuoteAssetVolume = node.getBigDecimal("Q")
        )
    }

}

data class AggregateTradeEvent(
    @SerializedName("E") override val eventTime: Instant,
    @SerializedName("s") val symbol: String,
    @SerializedName("a") val id: Long,
    @SerializedName("p") val price: BigDecimal,
    @SerializedName("q") val quantity: BigDecimal,
    @SerializedName("f") val firstTradeId: Long,
    @SerializedName("l") val lastTradeId: Long,
    @SerializedName("T") val time: Instant,
    @SerializedName("m") val isBuyerMaker: Boolean,
    @SerializedName("M") val isBestMatch: Boolean
) : WebSocketEvent

data class TradeEvent(
    @SerializedName("E") override val eventTime: Instant,
    @SerializedName("s") val symbol: String,
    @SerializedName("t") val id: Long,
    @SerializedName("p") val price: BigDecimal,
    @SerializedName("q") val quantity: BigDecimal,
    @SerializedName("b") val buyOrderId: Long,
    @SerializedName("a") val sellOrderId: Long,
    @SerializedName("T") val tradeTime: Instant,
    @SerializedName("m") val isBuyerMaker: Boolean,
    @SerializedName("M") val isBestMatch: Boolean
) : WebSocketEvent

data class MiniTicker(
    @SerializedName("E") override val eventTime: Instant,
    @SerializedName("s") val symbol: String,
    @SerializedName("c") val close: BigDecimal,
    @SerializedName("o") val open: BigDecimal,
    @SerializedName("h") val high: BigDecimal,
    @SerializedName("l") val low: BigDecimal,
    @SerializedName("v") val volume: BigDecimal,
    @SerializedName("q") val quoteAssetVolume: BigDecimal
) : WebSocketEvent

data class Ticker(
    @SerializedName("E") override val eventTime: Instant,
    @SerializedName("s") val symbol: String,
    @SerializedName("p") val priceChange: BigDecimal,
    @SerializedName("P") val priceChangePercent: BigDecimal,
    @SerializedName("w") val weightedAveragePrice: BigDecimal,
    @SerializedName("x") val firstTradePrice: BigDecimal,
    @SerializedName("c") val lastPrice: BigDecimal,
    @SerializedName("Q") val lastQuantity: BigDecimal,
    @SerializedName("b") val bestBidPrice: BigDecimal,
    @SerializedName("B") val bestBidQuantity: BigDecimal,
    @SerializedName("a") val bestAskPrice: BigDecimal,
    @SerializedName("A") val bestAskQuantity: BigDecimal,
    @SerializedName("o") val openPrice: BigDecimal,
    @SerializedName("h") val highPrice: BigDecimal,
    @SerializedName("l") val lowPrice: BigDecimal,
    @SerializedName("v") val volume: BigDecimal,
    @SerializedName("q") val quoteAssetVolume: BigDecimal,
    @SerializedName("O") val openTime: Instant,
    @SerializedName("C") val closeTime: Instant,
    @SerializedName("F") val firstTradeId: Long,
    @SerializedName("L") val lastTradeId: Long,
    @SerializedName("n") val totalTrades: Long
) : WebSocketEvent

data class AssetBalanceEvent(
    @SerializedName("a") val asset: String,
    @SerializedName("f") val free: BigDecimal,
    @SerializedName("l") val locked: BigDecimal
)

data class OutboundAccountPosition(
    @SerializedName("E") override val eventTime: Instant,
    @SerializedName("u") val lastUpInstant: Instant,
    @SerializedName("B") val balances: List<AssetBalanceEvent>
) : WebSocketEvent