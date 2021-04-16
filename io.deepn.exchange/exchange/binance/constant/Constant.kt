package io.deepn.exchange.exchange.binance.constant


const val API_KEY_HEADER = "X-MBX-APIKEY"

const val ENDPOINT_SECURITY_TYPE_APIKEY = "APIKEY"
const val KEY_HEADER = "$ENDPOINT_SECURITY_TYPE_APIKEY: #"

const val ENDPOINT_SECURITY_TYPE_SIGNED = "SIGNED"
const val SIGNED_HEADER = "$ENDPOINT_SECURITY_TYPE_SIGNED: #"

const val DEFAULT_RECEIVING_WINDOW = 5000L

val VALID_DEPTH_LIMITS = listOf(5, 10, 20, 50, 100, 500, 1000, 5000)

const val MAX_STREAM_SIZE = 1024