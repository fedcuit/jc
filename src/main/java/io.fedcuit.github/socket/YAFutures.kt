package io.fedcuit.github.socket

import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.CompletableFuture

fun completify(asyncSocket: AsynchronousSocketChannel, methodName: String, vararg params: Any): CompletableFuture<Any> {
    val future = CompletableFuture<Any>()

    val cb = object : CompletionHandler<Any, Any?> {
        override fun completed(result: Any, attachment: Any?) {
            future.complete(result)
        }

        override fun failed(exc: Throwable, attachment: Any?) {
            future.completeExceptionally(exc)
        }
    }


    val args = arrayOf(*params, cb)
    val method = asyncSocket.javaClass.methods.first {
        it.name == methodName
                && it.parameters.size == args.size
                && it.parameterTypes.last() == CompletionHandler::class.java
    }
    method.invoke(asyncSocket, *args)

    return future
}
