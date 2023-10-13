package underthehood.stateandvalue

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.*
import kotlin.coroutines.resume

/**
    A function resumed with a value
*/

/**
suspend fun printUser(token: String) {
    println("Before")
    val userId = getUserId(token) // suspending
    println("Got userId: $userId")
    val userName = getUserName(userId, token) // suspending
    println(User(userId, userName))
    println("After")
}
 */

//Under the hood

fun printUser(token: String, continuation: Continuation<*>): Any {
    val continuation = continuation as? PrintUserContinuation ?: PrintUserContinuation(
        continuation as Continuation<Unit>,
        token)

    var result: Result<Any>? = continuation.result
    var userId: String? = continuation.userId
    val userName: String

    if(continuation.label == 0) {
        println("Before")
        continuation.label = 1
        val res = getUserId(token, continuation)
        if (res == COROUTINE_SUSPENDED) {
            return COROUTINE_SUSPENDED
        }
        result = Result.success(res)
    }

    if(continuation.label == 1) {
        userId = result!!.getOrThrow() as String
        println("Got userId: $userId")
        continuation.label = 2
        continuation.userId = userId
        val res = getUserName(userId, continuation)
        if(res == COROUTINE_SUSPENDED) {
            return COROUTINE_SUSPENDED
        }
    }

    if(continuation.label == 2) {
        userName = result?.getOrThrow() as String
        println(User(userId as String, userName))
        println("After")
        return Unit
    }

    error("Impossible")
}

private val executor = Executors.newSingleThreadScheduledExecutor {
    Thread(it, "scheduler").apply { isDaemon = true }
}

private fun getUserId(token: String, continuation: Continuation<String>): Any {
    executor.schedule({ continuation.resume("SomeId") }, 1000, TimeUnit.MILLISECONDS)
    return COROUTINE_SUSPENDED
}

private fun getUserName(userId: String, continuation: Continuation<String>): Any {
    executor.schedule( {
        continuation.resume("SomeName")
    }, 1000, TimeUnit.MILLISECONDS)
    return COROUTINE_SUSPENDED
}

private class PrintUserContinuation(
    val completion: Continuation<Unit>,
    val token: String
): Continuation<String> {
    override val context: CoroutineContext
        get() = completion.context

    var label = 0
    var result: Result<Any>? = null
    var userId: String? = null

    override fun resumeWith(result: Result<String>) {
        this.result = result
        val res = try {
            val r = printUser(token, this)
            if(r == COROUTINE_SUSPENDED) return
            Result.success(r as Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
        completion.resumeWith(res)
    }
}


data class User(val id: String, val name: String)