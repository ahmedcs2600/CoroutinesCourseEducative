package sequence

import kotlin.random.Random

fun randomNumbers(
    seed: Long = System.currentTimeMillis()
): Sequence<Int> = sequence {
    val rand = Random(seed)
    while(true) {
        yield(rand.nextInt())
    }
}
fun main() {
    println(randomNumbers().take(10).toList())
}
