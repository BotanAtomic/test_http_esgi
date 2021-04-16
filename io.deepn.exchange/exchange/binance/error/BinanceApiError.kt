package io.deepn.exchange.exchange.binance.error

import com.google.gson.annotations.SerializedName

const val INTERNAL_ERROR = -1
const val UNKNOWN = -1000
const val DISCONNECTED = -1001
const val UNAUTHORIZED = -1002
const val TOO_MANY_REQUESTS = -1003
const val UNEXPECTED_RESP = -1006
const val TIMEOUT = -1007
const val UNKNOWN_ORDER_COMPOSITION = -1014
const val TOO_MANY_ORDERS = -1015
const val SERVICE_SHUTTING_DOWN = -1016
const val UNSUPPORTED_OPERATION = -1020
const val INVALID_TIMESTAMP = -1021
const val INVALID_SIGNATURE = -1022
const val ILLEGAL_CHARS = -1100
const val TOO_MANY_PARAMETERS = -1101
const val MANDATORY_PARAM_EMPTY_OR_MALFORMED = -1102
const val UNKNOWN_PARAM = -1103
const val UNREAD_PARAMETERS = -1104
const val PARAM_EMPTY = -1105
const val PARAM_NOT_REQUIRED = -1106
const val BAD_PRECISION = -1111
const val NO_DEPTH = -1112
const val TIF_NOT_REQUIRED = -1114
const val INVALID_TIF = -1115
const val INVALID_ORDER_TYPE = -1116
const val INVALID_SIDE = -1117
const val EMPTY_NEW_CL_ORD_ID = -1118
const val EMPTY_ORG_CL_ORD_ID = -1119
const val BAD_INTERVAL = -1120
const val BAD_SYMBOL = -1121
const val INVALID_LISTEN_KEY = -1125
const val MORE_THAN_XX_HOURS = -1127
const val OPTIONAL_PARAMS_BAD_COMBO = -1128
const val INVALID_PARAMETER = -1130
const val NEW_ORDER_REJECTED = -2010
const val CANCEL_REJECTED = -2011
const val NO_SUCH_ORDER = -2013
const val BAD_API_KEY_FMT = -2014
const val REJECTED_MBX_KEY = -2015
const val NO_TRADING_WINDOW = -2016

fun Int.toBinanceException() = BinanceApiError(this).toException()

class BinanceApiException(error: BinanceApiError = BinanceApiError()): Throwable("${error.code}: ${error.message}")

data class BinanceApiError(
    val code: Int = INTERNAL_ERROR,
    @SerializedName("msg") val message: String? = null
) {
    fun isInternal() = code == INTERNAL_ERROR
}

fun BinanceApiError.toException() = BinanceApiException(this)