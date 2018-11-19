package io.fedcuit.github.futurePattern

import java.util.concurrent.CompletableFuture

class SomeService {
    fun request(params: String): Data<String> {
        val futureData = FutureData()

        CompletableFuture.runAsync {
            val realData = RealData(params)
            futureData.set(realData.get())
        }

        return futureData
    }
}

fun main(args: Array<String>) {
    val someService = SomeService()
    val response = someService.request("T")

    println("Request sent")
    println("Response is ${response.get()}")
}
