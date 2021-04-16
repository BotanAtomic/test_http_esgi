package io.deepn.exchange.exchange.binance.entity.market

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.math.BigDecimal

data class OrderBookEntry(val price: BigDecimal, val quantity: BigDecimal)

data class OrderBook(
    val lastUpdateId: Long,
    val bids: List<OrderBookEntry>,
    val asks: List<OrderBookEntry>
)

class OrderBookDeserializer : JsonDeserializer<OrderBook?> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): OrderBook? {
        if (json == null) return null
        val data = json.asJsonObject

        fun toOrderBookEntries(array: JsonArray?): List<OrderBookEntry> {
            if (array == null) return ArrayList()
            return array
                .map { it.asJsonArray }
                .map { OrderBookEntry(it[0].asBigDecimal, it[1].asBigDecimal) }
        }

        return OrderBook(
            data.get("lastUpdateId").asLong,
            toOrderBookEntries(data.getAsJsonArray("bids")),
            toOrderBookEntries(data.getAsJsonArray("asks"))
        )
    }

}