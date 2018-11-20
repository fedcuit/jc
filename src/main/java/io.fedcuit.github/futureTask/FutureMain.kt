package io.fedcuit.github.futureTask;


import java.util.concurrent.CompletableFuture
import java.util.concurrent.FutureTask


fun main(args: Array<String>) {
    val futureTask = FutureTask<String>(RealData("T"));
    CompletableFuture.runAsync(futureTask)
    println("Request sent")

    println("Response is: ${futureTask.get()}")
}
