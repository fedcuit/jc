package io.fedcuit.github;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class AtomicFieldUpdater {

    private static final int TIMES = 100000;
    private static final ListeningExecutorService es = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    static class Candidate {
        volatile int score;
    }

    List<Integer> runWithAtomicFieldUpdater() throws InterruptedException {
        Candidate candidate = new Candidate();

        AtomicInteger score = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicIntegerFieldUpdater<Candidate> fieldUpdater = AtomicIntegerFieldUpdater.newUpdater(Candidate.class, "score");

        List<? extends ListenableFuture<?>> futures = IntStream
                .range(0, TIMES)
                .mapToObj(i -> es.submit(() -> {
                    if (Math.random() < 0.4) {
                        // Reflection
                        fieldUpdater.incrementAndGet(candidate);
                        score.incrementAndGet();

                    }
                })).collect(Collectors.toList());

        Futures.whenAllComplete(futures).run(countDownLatch::countDown, es);

        countDownLatch.await();
        return Arrays.asList(candidate.score, score.get());
    }

    List<Integer> runWithReentrantLock() throws InterruptedException {
        Candidate candidate = new Candidate();

        ReentrantLock lock = new ReentrantLock();
        AtomicInteger score = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        List<? extends ListenableFuture<?>> futures = IntStream
                .range(0, TIMES)
                .mapToObj(i -> es.submit(() -> {
                    if (Math.random() < 0.4) {
                        lock.lock();
                        try {
                            candidate.score++;
                        } finally {
                            lock.unlock();
                        }
                        score.incrementAndGet();
                    }
                })).collect(Collectors.toList());

        Futures.whenAllComplete(futures).run(countDownLatch::countDown, es);
        countDownLatch.await();
        return Arrays.asList(candidate.score, score.get());
    }

    List<Integer> runWithIntrinsicLock() throws InterruptedException {
        Candidate candidate = new Candidate();

        Object lock = new Object();
        AtomicInteger score = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        List<? extends ListenableFuture<?>> futures = IntStream
                .range(0, TIMES)
                .mapToObj(i -> es.submit(() -> {
                    if (Math.random() < 0.4) {
                        synchronized (lock) {
                            candidate.score++;
                        }
                        score.incrementAndGet();
                    }
                })).collect(Collectors.toList());

        Futures.whenAllComplete(futures).run(countDownLatch::countDown, es);
        countDownLatch.await();

        return Arrays.asList(candidate.score, score.get());
    }

    List<Integer> runWithAtomicInteger() throws InterruptedException {
        AtomicInteger score = new AtomicInteger();
        AnotherCandidate anotherCandidate = new AnotherCandidate();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        List<? extends ListenableFuture<?>> futures = IntStream
                .range(0, TIMES)
                .mapToObj(i -> es.submit(() -> {
                    if (Math.random() < 0.4) {
                        anotherCandidate.score.incrementAndGet();
                        score.incrementAndGet();
                    }
                })).collect(Collectors.toList());

        Futures.whenAllComplete(futures).run(countDownLatch::countDown, es);
        countDownLatch.await();

        return Arrays.asList(anotherCandidate.score.get(), score.get());
    }

    static class AnotherCandidate {
        AtomicInteger score = new AtomicInteger();
    }
}