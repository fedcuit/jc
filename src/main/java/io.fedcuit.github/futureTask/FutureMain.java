package io.fedcuit.github.futureTask;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FutureMain {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        FutureTask<String> futureTask = new FutureTask<>(new RealData("T"));

        CompletableFuture.runAsync(futureTask);

        System.out.println("Request sent");

        Thread.sleep(2000);

        System.out.printf("Response is: %s", futureTask.get());
    }
}
