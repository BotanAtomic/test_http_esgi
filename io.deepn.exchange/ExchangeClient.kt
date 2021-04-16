package io.deepn.exchange

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import io.deepn.exchange.adapter.ReactorCallAdapterFactory
import io.deepn.exchange.dispatcher.DelayedDispatcher
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass

abstract class ExchangeClient(
    baseUrl: String,
    gson: GsonBuilder = GsonBuilder(),
    httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
) {

    private val converterFactory = GsonConverterFactory.create(gson.setLenient().create())

    private val dispatcher = Dispatcher().apply {
        maxRequests = 500
        maxRequestsPerHost = 500
    }

    protected val serviceProvider: Retrofit = Retrofit.Builder()
        .client(httpClient.dispatcher(dispatcher).build())
        .addCallAdapterFactory(ReactorCallAdapterFactory.create())
        .addConverterFactory(converterFactory)
        .baseUrl(baseUrl)
        .build()

    protected fun <T : Any> createService(serviceClass: KClass<T>): T = serviceProvider.create(serviceClass.java)

}

abstract class ExchangeWebSocketClient(
    private val baseUrl: String,
    gsonBuilder: GsonBuilder = GsonBuilder(),
    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder(),
) : WebSocketListener() {

    protected val connected = AtomicBoolean(false)

    protected val gson: Gson = gsonBuilder.create()

    private val messageDispatcher = DelayedDispatcher(500)

    private val dispatcher = Dispatcher().apply {
        maxRequests = 1
        maxRequestsPerHost = 1
    }

    protected fun <T : Any> JsonElement.convert(dataClass: Type): T {
        return gson.fromJson(this, dataClass)
    }

    protected fun <T : Any> JsonElement.convert(dataClass: KClass<T>): T {
        return gson.fromJson(this, dataClass.java)
    }

    lateinit var webSocket: WebSocket

    protected fun connect() {
        webSocket = httpClient.dispatcher(
            dispatcher
        ).build().newWebSocket(
            Request.Builder().url(baseUrl).build(),
            this
        )
    }


    protected fun Any.send() {
        messageDispatcher.dispatch {
            webSocket.send(gson.toJson(this))
        }
    }

    fun isConnected() = connected.get()

}