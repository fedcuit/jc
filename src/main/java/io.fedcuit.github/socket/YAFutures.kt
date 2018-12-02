package io.fedcuit.github.socket

import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.CompletableFuture

fun AsynchronousSocketChannel.readAsync(bb: ByteBuffer, attachment: Any?): CompletableFuture<Int> {
    val deferred = CompletableFuture<Int>()
    this.read(bb, attachment, object : CompletionHandler<Int, Any?> {
        override fun completed(result: Int?, attachment: Any?) {
            deferred.complete(result)
        }

        override fun failed(exc: Throwable?, attachment: Any?) {
            deferred.completeExceptionally(exc)
        }
    })
    return deferred
}

fun AsynchronousSocketChannel.writeAsync(bb: ByteBuffer, attachment: Any?): CompletableFuture<Int> {
    val deferred = CompletableFuture<Int>()
    this.write(bb, attachment, object : CompletionHandler<Int, Any?> {
        override fun completed(result: Int?, attachment: Any?) {
            deferred.complete(result)
        }

        override fun failed(exc: Throwable?, attachment: Any?) {
            deferred.completeExceptionally(exc)
        }
    })
    return deferred
}