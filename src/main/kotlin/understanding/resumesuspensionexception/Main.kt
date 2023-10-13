package understanding.resumesuspensionexception

import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun main() {
    runCatching { resumeWithException() }.onFailure(::println).onSuccess {
        println("Success")
    }
}

suspend fun resumeWithException() = suspendCoroutine<Unit> { continuation ->
    continuation.resumeWithException(Exception("Error!"))
}
