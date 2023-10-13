package sequence
import kotlin.random.Random

fun randomUniqueStrings(
    length: Int,
    seed: Long = System.currentTimeMillis()
): Sequence<String> = sequence {
    val rand = Random(seed)
    val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    while(true) {
        val randomString = (1..length).map { rand.nextInt(charPool.size) }.map { charPool[it] }.joinToString("")
        yield(randomString)
    }
}

fun main() {
    println(randomUniqueStrings(10).take(10).toList())
}
