package io.fedcuit.github.futureTask;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class RealData implements Callable<String> {
    private final String params;

    RealData(final String params) {
        this.params = params;
    }

    @Override
    public String call() {
        return IntStream.range(0, 10).mapToObj(i -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return this.params;
        }).collect(Collectors.joining());
    }
}