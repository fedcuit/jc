package io.fedcuit.github

import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import com.google.common.util.concurrent.SettableFuture
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Supplier


class Candidate {
    @Volatile
    @JvmField
    var score = 0
}

class AtomicFieldUpdater {
    private val times: Int = 100000
    private val es: ListeningExecutorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool())

    @Throws(InterruptedException::class, ExecutionException::class)
    fun runWithAtomicFieldUpdater(): Pair<Int, Int> {
        val candidate = Candidate()

        val score = AtomicInteger()
        val settableFuture: SettableFuture<Int> = SettableFuture.create()
        // seems like something wrong with reflection in kotlin
        // fixed @JvmField makes the Kotlin-compiler expose the property as a field on the JVM.
        // See here: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-field/
        val fieldUpdater = AtomicIntegerFieldUpdater.newUpdater(candidate.javaClass, "score")

        val futures = (1..times).map { i ->
            es.submit {
                if (Math.random() < 0.4) {
                    // Reflection
                    fieldUpdater.incrementAndGet(candidate)
                    score.incrementAndGet()

                }
            }
        }.toList()

        Futures.whenAllComplete(futures).run(Runnable { settableFuture.set(null) }, es)

        settableFuture.get()

        return Pair(candidate.score, score.get())
    }


    @Throws(InterruptedException::class)
    fun runWithReentrantLock(): Pair<Int, Int> {
        val candidate = Candidate()

        val lock = ReentrantLock()
        val score = AtomicInteger()
        val sentinel = Object()

        val futures = (1..times).map {
            es.submit {
                if (Math.random() < 0.4) {
                    lock.lock()
                    try {
                        candidate.score++
                    } finally {
                        lock.unlock()
                    }
                    score.incrementAndGet()
                }
            }

        }.toList()

        Futures.whenAllComplete(futures).run(Runnable {
            // Hold the monitor of sentinel object before call notify()
            synchronized(sentinel) {
                sentinel.notify()
            }
        }, es)

        // Hold the monitor of sentinel object before call wait()
        synchronized(sentinel) {
            // await in a loop with condition check to prevent spurious wakeup
            while (futures.size != times) {
                sentinel.wait()
            }
            return Pair(candidate.score, score.get())
        }
    }

    @Throws(InterruptedException::class)
    fun runWithIntrinsicLock(): Pair<Int, Int> {
        val candidate = Candidate()

        val lock = Any()
        val score = AtomicInteger()

        val futures = (1..times).map {
            CompletableFuture.supplyAsync<Any>(
                    Supplier {
                        if (Math.random() < 0.4) {
                            synchronized(lock) {
                                candidate.score++
                            }
                            score.incrementAndGet()
                        }
                        null
                    },
                    es
            )
        }.toList()


        val allOf: CompletableFuture<Void> = CompletableFuture.allOf(*futures.toTypedArray())
        allOf.join()
        return Pair(candidate.score, score.get())
    }

    @Throws(InterruptedException::class)
    fun runWithAtomicInteger(): Pair<Int, Int> {
        val score = AtomicInteger()
        val anotherCandidate = AnotherCandidate()
        val semaphore = Semaphore(1)
        semaphore.acquire()

        val futures = (1..times).map {
            es.submit {
                if (Math.random() < 0.4) {
                    anotherCandidate.score.incrementAndGet()
                    score.incrementAndGet()
                }
            }
        }.toList()

        Futures.whenAllComplete(futures).run(Runnable { semaphore.release() }, es)

        semaphore.acquire()
        return Pair(anotherCandidate.score.get(), score.get())
    }

    class AnotherCandidate {
        val score = AtomicInteger()
    }
}
