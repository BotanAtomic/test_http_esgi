package io.deepn.exchange.exchange.binance.entity.market

import com.google.gson.annotations.SerializedName
import io.deepn.common.OrderSide
import io.deepn.common.OrderType
import java.math.BigDecimal
import java.time.Instant

enum class OrderStatus {
    NEW,
    PARTIALLY_FILLED,
    FILLED,
    CANCELED,
    PENDING_CANCEL,
    REJECTED,
    EXPIRED
}

enum class OrderResponseType {
    ACK,
    RESULT,
    FULL
}

enum class OrderRejectReason {
    NONE,
    UNKNOWN_INSTRUMENT,
    MARKET_CLOSED,
    PRICE_QTY_EXCEED_HARD_LIMITS,
    UNKNOWN_ORDER,
    DUPLICATE_ORDER,
    UNKNOWN_ACCOUNT,
    INSUFFICIENT_BALANCE,
    ACCOUNT_INACTIVE,
    ACCOUNT_CANNOT_SETTLE,
    ORDER_WOULD_TRIGGER_IMMEDIATELY
}

enum class OCOStatus {
    RESPONSE,
    EXEC_STARTED,
    ALL_DONE
}

enum class OCOOrderStatus {
    EXECUTING,
    ALL_DONE,
    REJECT
}

enum class TimeInForce {
    GTC,
    IOC,
    FOK
}

data class NewOrder(
    val symbol: String,
    val orderId: Long,
    val orderListId: Long,
    val clientOrderId: String,
    val transactionTime: Instant,
    val price: BigDecimal,
    @SerializedName("origQty") val originalQuantity: BigDecimal,
    @SerializedName("executedQty") val executedQuantity: BigDecimal,
    @SerializedName("cummulativeQuoteQty") val cummulativeQuoteQuantity: BigDecimal,
    val status: OrderStatus,
    val timeInForce: TimeInForce,
    val side: OrderSide,
    val fills: List<Trade>
)

data class Order(
    val symbol: String,
    val orderId: Long,
    val orderListId: Long,
    val clientOrderId: String,
    val price: BigDecimal,
    @SerializedName("origQty") val originalQuantity: BigDecimal,
    @SerializedName("executedQty") val executedQuantity: BigDecimal,
    @SerializedName("cummulativeQuoteQty") val cummulativeQuoteQuantity: BigDecimal,
    val status: OrderStatus,
    val timeInForce: TimeInForce,
    val type: OrderType,
    val side: OrderSide,
    val stopPrice: BigDecimal,
    val time: Instant,
    val updateTime: Instant,
    val isWorking: Boolean,
    @SerializedName("origQuoteOrderQty") val originalQuoteQuantity: BigDecimal
)

data class CancelOrder(
    val symbol: String,
    @SerializedName("origClientOrderId") val originalClientOrderId: String,
    val orderId: Long,
    val orderListId: Long,
    val clientOrderId: String,
    val price: BigDecimal,
    @SerializedName("origQty") val originalQuantity: BigDecimal,
    @SerializedName("executedQty") val executedQuantity: BigDecimal,
    @SerializedName("cummulativeQuoteQty") val cummulativeQuoteQuantity: BigDecimal,
    val status: OrderStatus,
    val timeInForce: TimeInForce,
    val type: OrderType,
    val side: OrderSide
)