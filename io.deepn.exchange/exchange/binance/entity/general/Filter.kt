package io.deepn.exchange.exchange.binance.entity.general

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

enum class FilterType {
    PRICE_FILTER,
    LOT_SIZE,
    MIN_NOTIONAL,
    MAX_NUM_ORDERS,
    MAX_ALGO_ORDERS,
    MAX_NUM_ALGO_ORDERS,
    ICEBERG_PARTS,
    PERCENT_PRICE,
    MARKET_LOT_SIZE,
    MAX_NUM_ICEBERG_ORDERS,

    EXCHANGE_MAX_NUM_ORDERS,
    EXCHANGE_MAX_ALGO_ORDERS
}

data class SymbolFilter(
    val filterType: FilterType,

    @SerializedName("minPrice")
    val minimumPrice: BigDecimal,
    @SerializedName("maxPrice")
    val maximumPrice: BigDecimal,
    val tickSize: BigDecimal,

    val multiplierUp: BigDecimal,
    val multiplierDown: BigDecimal,

    @SerializedName("avgPriceMins")
    val averagePriceMins: BigDecimal,

    @SerializedName("minQty")
    val minimumQuantity: BigDecimal,
    @SerializedName("maxQty")
    val maximumQuantity: BigDecimal,
    val stepSize: BigDecimal,

    @SerializedName("minNotional")
    val minimumNotional: BigDecimal,
    val applyToMarket: Boolean,

    @SerializedName("maxNumAlgoOrders")
    val maximumNumberAlgoOrders: BigDecimal,

    @SerializedName("maxNumIcebergOrders")
    val maximumNumberIcebergOrders: BigDecimal,

    @SerializedName("maxPosition")
    val maximumPosition: BigDecimal,

    val limit: BigDecimal
)