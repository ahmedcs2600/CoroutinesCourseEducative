package sequence

fun computeFactorial(number: Long): Long {
    if(number == 0L) return 1
    var num = number
    var fact = 1L
    while(num > 0) {
        fact *= num
        num--
    }
    return fact
}
