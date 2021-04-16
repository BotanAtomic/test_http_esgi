package io.deepn.exchange.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass


fun gsonWithAdapters(vararg adapters: Pair<KClass<out Any>, Any>): GsonBuilder {
    return GsonBuilder().apply {
        adapters.forEach { (key, value) -> registerTypeAdapter(key.java, value) }
    }
}

fun List<Int>.closestValue(value: Int?) = if (value != null) minByOrNull { abs(value - it) } else null

fun Int?.between(min: Int, max: Int) = this.min(min).max(max)

fun Int?.max(value: Int) = if (this == null) null else min(value, this)

fun Int?.min(value: Int = 0) = if (this == null) null else max(value, this)

fun validateDates(
    startTime: Instant?,
    endTime: Instant?,
    maximumDelta: Duration,
    exception: Throwable
): Mono<Throwable>? {
    if (startTime != null && endTime != null) {
        if (Duration.between(startTime, endTime).minus(maximumDelta)
                .let { it.isZero || it.isNegative } || startTime.isAfter(endTime)
        ) return Mono.error(exception)
    }
    return null
}

fun JsonObject.getInstant(key: String): Instant = Instant.ofEpochMilli(getLong(key))

fun JsonObject.getLong(key: String): Long = this.get(key).asLong

fun JsonObject.getString(key: String): String = this.get(key).asString

fun JsonObject.getBoolean(key: String): Boolean = this.get(key).asBoolean

fun JsonObject.getBigDecimal(key: String): BigDecimal = this.get(key).asBigDecimal

fun <T> directMulticastBestEffort(): Sinks.Many<T> = Sinks.many().multicast().directBestEffort()