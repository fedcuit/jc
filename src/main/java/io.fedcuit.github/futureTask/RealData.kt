package io.fedcuit.github.futureTask

import java.util.concurrent.Callable

class RealData(private val params: String) : Callable<String> {
    override fun call(): String {
        return (0..10).joinToString {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            params
        }
    }
}