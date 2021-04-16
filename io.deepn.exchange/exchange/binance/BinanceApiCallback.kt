package io.deepn.exchange.exchange.binance

import io.deepn.exchange.exchange.binance.error.BinanceApiError
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response


class BinanceApiResponse<T>(bodyConverter: Converter<ResponseBody, BinanceApiError>, private val response: Response<T>) {

    var body: T? = null
    var errorBody: BinanceApiError? = null

    init {
        if (response.isSuccessful) body = response.body()
        else {
            val error = response.errorBody()
            if (response.code() >= 500) errorBody = BinanceApiError()
            else if (error != null) {
                errorBody = kotlin.runCatching { bodyConverter.convert(error) }
                    .getOrElse { BinanceApiError(response.code()) }
            }
        }
    }

    fun isError() = response.isSuccessful.not()

    override fun toString() = "{ isSuccess: ${isError()}, body: $body, errorBody: $errorBody }"
}
