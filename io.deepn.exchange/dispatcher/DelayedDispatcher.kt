package io.deepn.exchange.dispatcher

import reactor.core.Disposable
import reactor.core.scheduler.Schedulers
import java.time.Instant
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

typealias Runnable = () -> Unit

class DelayedDispatcher(private val delay: Long) {

    private val synchronizedRunnable = ConcurrentLinkedDeque<Runnable>()
    private val scheduler = Schedulers.single()

    private val currentTask = AtomicReference<Disposable>()

    private val lastDispatch = AtomicReference<Instant>()

    fun dispatch(runnable: Runnable) {
        synchronizedRunnable.addFirst(runnable)
        if (currentTask.get() == null || currentTask.get().isDisposed)
            dispatchNext()
    }

    private fun dispatchNext() {
        val applyDelay =
            lastDispatch.get() != null && (Instant.now().toEpochMilli() - lastDispatch.get().toEpochMilli()) < delay
        val runnable = synchronizedRunnable.pollLast()
        if (runnable != null) {
            currentTask.set(scheduler.schedule({
                runnable()
                lastDispatch.set(Instant.now())
                dispatchNext()
            }, if (applyDelay) delay else 0, TimeUnit.MILLISECONDS))
        }
    }

}