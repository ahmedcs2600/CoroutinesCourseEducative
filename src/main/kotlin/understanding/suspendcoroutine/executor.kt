package understanding.suspendcoroutine

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private val executor = Executors.newSingleThreadScheduledExecutor {
    Thread(it, "scheduler").apply { isDaemon = true }
}

suspend fun main() {
    println("Before")
    delay(1000L)
}


private suspend fun delay(time: Long) {
    suspendCoroutine { continuation ->
        executor.schedule({
            continuation.resume(Unit)
        }, time, TimeUnit.MILLISECONDS)
    }
}