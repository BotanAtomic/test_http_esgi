package io.deepn.exchange.exchange.binance.security

import okhttp3.Interceptor
import okhttp3.Response

class WeightInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())


        return response
    }

}