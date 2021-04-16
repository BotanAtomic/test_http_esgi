package io.deepn.exchange.entity

import java.math.BigDecimal
import java.time.Instant
import java.util.*

interface CandleEntity {
    val openTime: Instant
    val closeTime: Instant
    val open: BigDecimal
    val high: BigDecimal
    val low: BigDecimal
    val close: BigDecimal
    val volume: BigDecimal
}