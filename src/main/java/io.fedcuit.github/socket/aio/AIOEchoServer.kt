package io.fedcuit.github.socket.aio

import java.lang.Thread.sleep
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.CompletableFuture

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

    handleConnection(asyncServer)
}

private fun handleConnection(asyncServer: AsynchronousServerSocketChannel) {
    val socketAccepted = CompletableFuture<AsynchronousSocketChannel>()
    asyncServer.accept(null, object : CompletionHandler<AsynchronousSocketChannel, Nothing?> {
        override fun completed(socketChannel: AsynchronousSocketChannel, attachment: Nothing?) {
            socketAccepted.complete(socketChannel)
        }

        override fun failed(exc: Throwable, attachment: Nothing?) {
            socketAccepted.completeExceptionally(exc)
        }
    })

    val readFinished = CompletableFuture<ByteBuffer>()
    socketAccepted.thenComposeAsync { asyncSocket ->
        val bb = ByteBuffer.allocate(1024)

        asyncSocket.read(bb, null, object : CompletionHandler<Int, Nothing?> {
            override fun completed(result: Int, attachment: Nothing?) {
                bb.flip()
                readFinished.complete(bb)
            }

            override fun failed(exc: Throwable?, attachment: Nothing?) {
                readFinished.completeExceptionally(exc)
            }
        })
        readFinished
    }

    val writeFinished = CompletableFuture<Int>()
    readFinished.thenComposeAsync { byteBuffer ->
        val socket = socketAccepted.get()
        socket.write(byteBuffer, null, object : CompletionHandler<Int, Nothing?> {
            override fun completed(result: Int?, attachment: Nothing?) {
                writeFinished.complete(result)
            }

            override fun failed(exc: Throwable?, attachment: Nothing?) {
                writeFinished.completeExceptionally(exc)
            }
        })
        writeFinished
    }
}