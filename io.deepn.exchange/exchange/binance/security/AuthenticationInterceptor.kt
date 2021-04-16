package io.deepn.exchange.exchange.binance.security

import io.deepn.exchange.exchange.binance.BinanceCredentials
import io.deepn.exchange.exchange.binance.constant.API_KEY_HEADER
import io.deepn.exchange.exchange.binance.constant.ENDPOINT_SECURITY_TYPE_APIKEY
import io.deepn.exchange.exchange.binance.constant.ENDPOINT_SECURITY_TYPE_SIGNED
import io.deepn.exchange.security.signHmacSHA256
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response


class AuthenticationInterceptor(private val credentials: BinanceCredentials) : Interceptor {

    override fun intercept(chain: Chain): Response {
        val original: Request = chain.request()
        val newRequestBuilder: Request.Builder = original.newBuilder()

        val isApiKeyRequired = original.header(ENDPOINT_SECURITY_TYPE_APIKEY) != null
        val isSignatureRequired = original.header(ENDPOINT_SECURITY_TYPE_SIGNED) != null

        newRequestBuilder
            .removeHeader(ENDPOINT_SECURITY_TYPE_APIKEY)
            .removeHeader(ENDPOINT_SECURITY_TYPE_SIGNED)

        if (isApiKeyRequired || isSignatureRequired)
            newRequestBuilder.addHeader(API_KEY_HEADER, credentials.apiKey)

        if (isSignatureRequired) {
            val payload = original.url().query()
            if (payload != null && payload.isNotBlank()) {
                val signature: String = signHmacSHA256(payload, credentials.secret)
                val signedUrl: HttpUrl = original.url().newBuilder().addQueryParameter("signature", signature).build()
                newRequestBuilder.url(signedUrl)
            }
        }
        val newRequest: Request = newRequestBuilder.build()
        return chain.proceed(newRequest)
    }


}