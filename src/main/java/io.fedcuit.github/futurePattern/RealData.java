package io.fedcuit.github.futurePattern;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RealData implements Data<String> {

    private final String result;

    RealData(final String param) {
        this.result = IntStream.range(0, 10).mapToObj(i -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return param;
        }).collect(Collectors.joining());
    }

    @Override
    public String get() {
        return this.result;
    }
}
