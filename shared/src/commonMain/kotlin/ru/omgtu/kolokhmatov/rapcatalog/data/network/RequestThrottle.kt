package ru.omgtu.kolokhmatov.rapcatalog.data.network

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

// Выпускает запросы по одному и не чаще раза в interval. Ждущий запрос
// можно отменить: delay и Mutex отменяемые, очередь от этого не встаёт.
internal class RequestThrottle(private val interval: Duration) {
    private val mutex = Mutex()
    private var lastSent: TimeMark? = null

    suspend fun <T> run(request: suspend () -> T): T {
        mutex.withLock {
            val wait = lastSent?.let { interval - it.elapsedNow() } ?: Duration.ZERO
            if (wait.isPositive()) delay(wait)
            lastSent = TimeSource.Monotonic.markNow()
        }
        return request()
    }
}
