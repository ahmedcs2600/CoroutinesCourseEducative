package underthehood.stateandvalue

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.*
import kotlin.coroutines.resume

/**
A function with a state
 */

/**
suspend fun myFunction() {
    println("Before")
    var counter = 0
    delay(1000L)
    counter++
    println("Counter: $counter")
    println("After")
}
 */

//Under the Hood

fun myFunction(continuation: Continuation<Unit>) {
    val continuation = continuation as? MyFunctionContinuation ?: MyFunctionContinuation(continuation)
    if(continuation.label == 0) {
        continuation.label = 1
        continuation.counter = 0
        println("Before")
        if(delay(1000L, continuation) == COROUTINE_SUSPENDED) {
            return
        }
    }

    if(continuation.label == 1) {
        continuation.label = 2
        continuation.counter++
        println("Counter: ${continuation.counter}")
        println("After")
        return Unit
    }

    error("Impossible")
}

private val executor = Executors.newSingleThreadScheduledExecutor {
    Thread(it, "scheduler")
}

private fun delay(timeInMilli: Long, continuation: MyFunctionContinuation): Any {
    executor.schedule({
        continuation.resume(Unit)
    } , timeInMilli, TimeUnit.MILLISECONDS)
    return COROUTINE_SUSPENDED
}

private class MyFunctionContinuation(private val completion: Continuation<Unit>): Continuation<Unit> {
    override val context: CoroutineContext
        get() = completion.context

    var result: Result<Unit>? = null
    var label = 0
    var counter = 0

    override fun resumeWith(result: Result<Unit>) {
        this.result = result
        val res = try {
            val r = myFunction(this)
            if(r != COROUTINE_SUSPENDED) {
                return
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
        completion.resumeWith(res)
    }
}
