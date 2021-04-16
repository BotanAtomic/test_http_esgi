package io.deepn.exchange.exchange.binance.entity.account

import java.math.BigDecimal
import java.time.Instant

data class AssetBalance(val asset: String, val free: BigDecimal, val locked: BigDecimal)

data class Account(
    val makerCommission: Int,
    val takerCommission: Int,
    val buyerCommission: Int,
    val sellerCommission: Int,
    val canTrade: Boolean,
    val canWithdraw: Boolean,
    val canDeposit: Boolean,
    val updateTime: Instant,
    val accountType: String,
    val balances: List<AssetBalance>,
    val permissions: List<String>
)