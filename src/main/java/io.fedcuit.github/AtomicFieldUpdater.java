package io.fedcuit.github;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

class AtomicFieldUpdater {
    static class Candidate {
        volatile int score;
    }

    List<Integer> runWithAtomicFieldUpdater() throws InterruptedException {
        Candidate candidate = new Candidate();

        AtomicInteger score = new AtomicInteger();
        AtomicIntegerFieldUpdater<Candidate> fieldUpdater = AtomicIntegerFieldUpdater.newUpdater(Candidate.class, "score");
        CountDownLatch countDownLatch = new CountDownLatch(10000);

        IntStream.range(0, 10000).mapToObj(i -> new Thread(() -> {
            if (Math.random() < 0.4) {
                // Reflection
                fieldUpdater.incrementAndGet(candidate);
                score.incrementAndGet();

            }
            countDownLatch.countDown();
        })).forEach(Thread::start);

        countDownLatch.await();
        return Arrays.asList(candidate.score, score.get());
    }


    List<Integer> runWithReentrantLock() throws InterruptedException {
        Candidate candidate = new Candidate();

        ReentrantLock lock = new ReentrantLock();
        AtomicInteger score = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(10000);

        IntStream.range(0, 10000).mapToObj(i -> new Thread(() -> {
            if (Math.random() < 0.4) {
                lock.lock();
                try {
                    candidate.score++;
                } finally {
                    lock.unlock();
                }
                score.incrementAndGet();
            }
            countDownLatch.countDown();
        })).forEach(Thread::start);

        countDownLatch.await();
        return Arrays.asList(candidate.score, score.get());
    }

    List<Integer> runWithIntrinsicLock() throws InterruptedException {
        Candidate candidate = new Candidate();

        Object lock = new Object();
        AtomicInteger score = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(10000);

        IntStream
                .range(0, 10000)
                .mapToObj(i -> new Thread(() -> {
                    if (Math.random() < 0.4) {
                        synchronized (lock) {
                            candidate.score++;
                        }
                        score.incrementAndGet();
                    }
                    countDownLatch.countDown();
                })).forEach(Thread::start);

        countDownLatch.await();

        return Arrays.asList(candidate.score, score.get());
    }
}