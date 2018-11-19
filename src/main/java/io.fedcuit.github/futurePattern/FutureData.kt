package io.fedcuit.github.futurePattern

import java.util.concurrent.locks.ReentrantLock

class FutureData : Data<String> {
    private val lock = ReentrantLock()
    private val valueIsSet = lock.newCondition()
    private var value: String = ""
    @Volatile
    private var isSet: Boolean = false

    fun set(value: String) {
        if (isSet) {
            // value can only be set once
            return
        }
        this.value = value
        // update flag
        isSet = true

        lock.lock()
        try {
            // notify all waiting threads
            valueIsSet.signalAll()
        } finally {
            lock.unlock()
        }
    }

    override fun get(): String {
        lock.lock()
        try {
            while (!isSet) {
                // block waiting if result is not set
                valueIsSet.await()
            }
        } finally {
            lock.unlock()
        }
        return value
    }
}