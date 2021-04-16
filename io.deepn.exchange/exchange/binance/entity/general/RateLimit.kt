package io.deepn.exchange.exchange.binance.entity.general

enum class RateLimitInterval {
    SECOND,
    MINUTE,
    DAY
}

enum class RateLimitType {
    RAW_REQUEST,
    REQUEST_WEIGHT,
    ORDERS
}

data class RateLimit(
    val rateLimitType: RateLimitType,
    val interval: RateLimitInterval,
    val intervalNum: Int,
    val limit: Int
)