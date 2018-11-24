package io.fedcuit.github.parallel.assemlyline

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

fun main(args: Array<String>) {
    val es = Executors.newFixedThreadPool(3)
    val countDownLatch = CountDownLatch(50 * 50)

    val start = System.currentTimeMillis()

    es.submit(Plus())
    es.submit(Multiply())
    es.submit(Div(countDownLatch))

    for (i in 1..50) for (j in 1..50) {
        val msg = Msg(i.toDouble(), j.toDouble(), "(($i + $j)*$i)/2")
        Plus.bq.add(msg)
    }

    countDownLatch.await()
    es.shutdownNow()

    println("Finished in ${System.currentTimeMillis() - start}ms")
}