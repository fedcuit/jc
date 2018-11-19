package io.fedcuit.github.futurePattern;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FutureData implements Data<String> {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition valueIsSet = lock.newCondition();
    private String value;
    private boolean isSet = false;

    synchronized void set(final String value) {
        if (isSet) {
            // value can only be set once
            return;
        }
        this.value = value;

        // update flag and notify all waiting threads
        this.isSet = true;
        valueIsSet.notifyAll();
    }

    @Override
    public synchronized String get() {
        while (!isSet) {
            try {
                // block waiting if result is not set
                valueIsSet.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return value;
    }
}
