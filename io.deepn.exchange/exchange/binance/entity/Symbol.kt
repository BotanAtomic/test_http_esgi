package io.deepn.exchange.exchange.binance.entity

import com.google.gson.annotations.SerializedName
import io.deepn.common.OrderType

enum class SymbolStatus {
    PRE_TRADING,
    TRADING,
    POST_TRADING,
    END_OF_DAY,
    HALT,
    AUCTION_MATCH,
    BREAK
}

data class Symbol(
    val symbol: String,
    val status: SymbolStatus,

    val baseAsset: String,
    val baseAssetPrecision: Int,

    val quoteAsset: String,
    val quotePrecision: Int,

    val baseCommissionPrecision: Int,
    val quoteCommissionPrecision: Int,

    val orderTypes: List<OrderType>,

    val icebergAllowed: Boolean,
    val ocoAllowed: Boolean,
    @SerializedName("quoteOrderQtyMarketAllowed")
    val quoteOrderQuantityMarketAllowed: Boolean,
    val isSpotTradingAllowed: Boolean,
    val isMarginTradingAllowed: Boolean,

    val permissions: List<String>
)