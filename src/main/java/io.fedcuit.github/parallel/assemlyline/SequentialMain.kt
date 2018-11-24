package io.fedcuit.github.parallel.assemlyline

private fun calculate(i: Int, j: Int): Msg {
    val msg = Msg(i.toDouble(), j.toDouble(), "(($i + $j)*$i)/2")
    msg.j = msg.i + msg.j
    Thread.sleep(1)
    msg.i = msg.j * msg.i
    Thread.sleep(1)
    msg.i = msg.i / 2
    Thread.sleep(1)
    return msg
}

fun main(args: Array<String>) {
    val start = System.currentTimeMillis()
    for (i in 1..50) for (j in 1..50) {
        val msg = calculate(i, j)
        println("${msg.orgStr} = ${msg.i}")
    }

    println("Finished in ${System.currentTimeMillis() - start}ms")
}
