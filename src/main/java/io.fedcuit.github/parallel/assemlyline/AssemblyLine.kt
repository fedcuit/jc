package io.fedcuit.github.parallel.assemlyline

import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue

class Msg(var i: Double, var j: Double, val orgStr: String)

class Plus : Runnable {
    override fun run() {
        while (true) {
            val msg = bq.take()
            msg.j = msg.i + msg.j
            Thread.sleep(1)
            Multiply.bq.add(msg)
        }
    }

    companion object {
        val bq = LinkedBlockingQueue<Msg>()
    }
}

class Multiply : Runnable {
    override fun run() {
        while (true) {
            val msg = bq.take()
            msg.i = msg.i * msg.j
            Thread.sleep(1)
            Div.bq.add(msg)
        }
    }

    companion object {
        val bq = LinkedBlockingQueue<Msg>()
    }
}

class Div(private val countDownLatch: CountDownLatch) : Runnable {
    override fun run() {
        while (true) {
            val msg = bq.take()
            msg.i = msg.i / 2
            Thread.sleep(1)
            println("${msg.orgStr} = ${msg.i}")
            countDownLatch.countDown()
        }
    }

    companion object {
        val bq = LinkedBlockingQueue<Msg>()
    }
}