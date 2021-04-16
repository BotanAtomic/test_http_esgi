package io.deepn.exchange.adapter

import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import reactor.core.CoreSubscriber
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.FluxSink.OverflowStrategy.LATEST
import reactor.core.publisher.Mono
import reactor.core.publisher.Operators
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.function.Consumer


class ReactorCallAdapterFactory(private val scheduler: Scheduler?) : CallAdapter.Factory() {

    companion object {

        fun create(scheduler: Scheduler? = null): CallAdapter.Factory = ReactorCallAdapterFactory(scheduler)

    }

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *> {
        if (getRawType(returnType) != Mono::class.java)
            throw IllegalStateException("Return type must be Mono<T>")

        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = getRawType(observableType)

        if (rawObservableType != Response::class.java)
            throw IllegalStateException("Response must be parametrized as Response<T>")

        val responseType = getParameterUpperBound(0, observableType as ParameterizedType)

        return ReactorCallAdapter<Any>(responseType, scheduler)

    }
}

class ReactorCallAdapter<T>(private val returnType: Type, private val scheduler: Scheduler?) : CallAdapter<T, Any> {

    override fun responseType(): Type = returnType

    override fun adapt(call: Call<T>): Any {
        val responseFlux = Flux.create(AsyncSinkConsumer(call), LATEST).apply {
            if (scheduler != null) subscribeOn(scheduler)
        }

        return ResultFlux(responseFlux).single()
    }
}

class AsyncSinkConsumer<T>(private val call: Call<T>) : Consumer<FluxSink<Response<T>>> {
    override fun accept(sink: FluxSink<Response<T>>) {
        DisposableCallback(call, sink).let {
            sink.onDispose(it)
            call.enqueue(it)
        }
    }
}

class DisposableCallback<T>(
    private val call: Call<T>,
    private val sink: FluxSink<Response<T>>
) : Callback<T>, Disposable {


    override fun onResponse(call: Call<T>, response: Response<T>) {
        sink.next(response)
        sink.complete()
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        sink.error(t)
    }

    override fun dispose() {
        call.cancel()
    }

    override fun isDisposed() = call.isCanceled

}

class ResultFlux<T>(private val upstream: Publisher<Response<T>>) : Flux<Response<T>>() {

    override fun subscribe(subscriber: CoreSubscriber<in Response<T>>) {
        upstream.subscribe(ResultSubscriber(subscriber))
    }

}

class ResultSubscriber<R>(
    private val subscriber: Subscriber<in Response<R>>
) : Subscriber<Response<R>> {

    override fun onSubscribe(subscription: Subscription) {
        subscriber.onSubscribe(subscription)
    }

    override fun onNext(response: Response<R>) {
        subscriber.onNext(response)
    }

    override fun onError(throwable: Throwable) {
        try {
            subscriber.onError(throwable)
        } catch (t: Throwable) {
            subscriber.onError(t)
            Operators.onErrorDropped(t, Context.empty());
            return
        }
    }

    override fun onComplete() {
        subscriber.onComplete()
    }
}

