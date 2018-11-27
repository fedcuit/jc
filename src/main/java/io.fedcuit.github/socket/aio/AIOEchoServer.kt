package io.fedcuit.github.socket.aio

import java.lang.Thread.sleep
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

private const val port = 8000

fun main(args: Array<String>) {
    start()

    while (true) {
        sleep(100)
    }
}

private fun start() {
    val asyncServer = AsynchronousServerSocketChannel.open().bind(InetSocketAddress(port))
    println("Server listen on $port")

    asyncServer.accept(null, object : CompletionHandler<AsynchronousSocketChannel, Nothing?> {
        val buffer = ByteBuffer.allocate(1024)

        override fun completed(socketChannel: AsynchronousSocketChannel, attachment: Nothing?) {
            var deferredWriting: Future<Int>? = null
            try {
                println(Thread.currentThread().name)
                buffer.clear()

                // async read for socket channel
                val deferredData = socketChannel.read(buffer)
                // blocking wait for read operation complete
                deferredData.get(100, TimeUnit.SECONDS)
                buffer.flip()

                deferredWriting = socketChannel.write(buffer)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: TimeoutException) {
                e.printStackTrace()
            } finally {
                // after writer operation is triggered, ready to accept new connection
                asyncServer.accept(null, this)

                // close this socket when writer is complete
                deferredWriting?.get()
                socketChannel.close()
            }
        }


        override fun failed(exc: Throwable, attachment: Nothing?) {
            println("Failed: $exc")
        }
    })
}
