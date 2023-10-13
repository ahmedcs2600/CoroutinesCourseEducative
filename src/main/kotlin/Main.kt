import kotlinx.coroutines.*


suspend fun test() = withTimeout(1500) {
    delay(1000)
    println("Still thinking")
    delay(1000)
    println("Done")
    42
}

suspend fun main(): Unit = coroutineScope {
    try { test() }
    catch (e: TimeoutCancellationException) { println("Canceled") }
    delay(1000)
}



