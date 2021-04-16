package io.deepn.exchange.exchange.binance.entity.general

import io.deepn.exchange.exchange.binance.entity.Symbol

data class ExchangeInfo(
    val timezone: String,
    val serverTime: Long,
    val rateLimits: List<RateLimit>,
    val symbols: List<Symbol>
)