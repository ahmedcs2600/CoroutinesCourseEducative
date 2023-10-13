package sequence

import java.math.BigInteger

val fibonacci = sequence {
    var first = BigInteger.valueOf(0)
    var second = BigInteger.valueOf(1)
    while(true) {
        yield(first)
        val tmp = first + second
        first = second
        second = tmp
    }
}

fun main() {
    println(fibonacci.take(10).toList())
}
