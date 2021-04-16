package io.deepn.exchange.exchange.binance.entity.market

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import io.deepn.exchange.entity.CandleEntity
import java.lang.reflect.Type
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class Candle(
    override val openTime: Instant,
    override val open: BigDecimal,
    override val high: BigDecimal,
    override val low: BigDecimal,
    override val close: BigDecimal,
    override val volume: BigDecimal,
    override val closeTime: Instant,
    val quoteAssetVolume: BigDecimal,
    val numberOfTrades: Long,
    val takerBaseVolume: BigDecimal,
    val takerQuoteVolume: BigDecimal
) : CandleEntity

class CandleDeserializer : JsonDeserializer<Candle?> {

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext): Candle? {
        val data = json.asJsonArray
        if (data.size() < 11) return null
        return Candle(
            Instant.ofEpochMilli(data[0].asLong),
            data[1].asBigDecimal,
            data[2].asBigDecimal,
            data[3].asBigDecimal,
            data[4].asBigDecimal,
            data[5].asBigDecimal,
            Instant.ofEpochMilli(data[6].asLong),
            data[7].asBigDecimal,
            data[8].asLong,
            data[9].asBigDecimal,
            data[10].asBigDecimal
        )
    }

}