package io.fedcuit.github.socket.aio

import io.fedcuit.github.socket.completify
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

    socketAccepted.thenComposeAsync { socket ->
        val bb = ByteBuffer.allocate(1024)
        completify(socket, "read", bb, 0)
                .thenComposeAsync { completify(socket, "write", bb.flip(), 0) }
    }
}
