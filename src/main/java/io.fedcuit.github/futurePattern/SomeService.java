package io.fedcuit.github.futurePattern;

import java.util.concurrent.CompletableFuture;

public class SomeService {
    public static void main(String[] args) {
        SomeService someService = new SomeService();
        Data data = someService.request("T");

        System.out.println("Request sent");

        try {
            // simulate run other tasks at the same time
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Response is: %s", data.get());
    }

    private Data request(final String params) {
        FutureData futureData = new FutureData();
        CompletableFuture.runAsync(() -> {
            RealData realData = new RealData(params);
            futureData.set(realData.get());
        });
        return futureData;
    }
}
