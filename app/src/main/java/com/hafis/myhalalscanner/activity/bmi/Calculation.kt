package com.hafis.myhalalscanner.activity.bmi


fun main(args: Array<String>) {
    val rows = 4
    for (i in rows downTo 1) {
        for (space in 1..rows - i) {
            print("  ")
        }
        for (j in i until 2 * i) {
            print("* ")
        }
        for (j in 0 until i - 1) {
            print("* ")
        }
        println()
    }

    var a = 7
    var b = 5
    print("Init: a = $a b = $b")
    run { val temp = a; a = b; b = temp }
    println("Result: a = $a b = $b")


    a = b.also { b = a }
    print("Result: a = $a b = $b")
}

private fun createSwapWithoutTemp() {
    var a = 10
    var b = 3
    print("Init: a = $a b = $b")
    a = b.also { b = a }
    println("Result: a = $a b = $b")
}

private fun createStar() {
    val rows = 4
    for (i in rows downTo 1) {
        for (space in 1..rows - i) {
            print("  ")
        }
        for (j in i until 2 * i) {
            print("* ")
        }
        for (j in 0 until i - 1) {
            print("* ")
        }
        println()
    }
}
