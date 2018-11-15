package io.fedcuit.github.futurePattern;

public class FutureData implements Data<String> {
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
        this.notifyAll();
    }

    @Override
    public synchronized String get() {
        while (!isSet) {
            try {
                // block waiting if result is not set
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return value;
    }
}
