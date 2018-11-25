package io.fedcuit.github.socket.threaded

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.locks.LockSupport

class SlowClient(private val msg: String, private val countDownLatch: CountDownLatch) : Runnable {
    override fun run() {
        val begin = System.currentTimeMillis()
        val client = Socket()
        client.connect(InetSocketAddress(8000))
        client.use { socket ->
            PrintWriter(socket.getOutputStream(), true).use { writer ->
                msg.forEach {
                    writer.print(it)
                    println("Send to Server: $it")
                    LockSupport.parkNanos(sleepTime)
                }
                writer.println()

                BufferedReader(InputStreamReader(socket.getInputStream())).use { reader ->
                    println("Echo from server: ${reader.readLine()}")
                }
            }
        }
        println("Thread ${Thread.currentThread().name} is occupied for ${System.currentTimeMillis() - begin}ms")
        countDownLatch.countDown()
    }

    companion object {
        const val sleepTime = 1000L * 1000 * 1000
    }
}

fun main(args: Array<String>) {
    val n = 5
    val tp = Executors.newCachedThreadPool()
    val countDownLatch = CountDownLatch(n)

    for (i in 1..n) {
        tp.submit(SlowClient("Hello", countDownLatch))
    }
    countDownLatch.await()
    tp.shutdownNow()
}