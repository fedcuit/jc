package io.fedcuit.github;

import kotlin.Pair;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class AtomicFieldUpdaterTest {
    @Test
    public void testWithIntrinsicLock() throws InterruptedException {
        AtomicFieldUpdater atomicFieldUpdater = new AtomicFieldUpdater();

        Pair<Integer, Integer> results = atomicFieldUpdater.runWithIntrinsicLock();

        System.out.println(results);
        assertEquals(results.getFirst(), results.getSecond());
    }

    @Test
    public void testWithReentrantLock() throws InterruptedException {
        AtomicFieldUpdater atomicFieldUpdater = new AtomicFieldUpdater();

        Pair<Integer, Integer> results = atomicFieldUpdater.runWithReentrantLock();

        System.out.println(results);
        assertEquals(results.getFirst(), results.getSecond());
    }

    @Ignore
    public void testWithAtomicFieldUpdater() throws InterruptedException, ExecutionException {
        AtomicFieldUpdater atomicFieldUpdater = new AtomicFieldUpdater();

        Pair<Integer, Integer> results = atomicFieldUpdater.runWithAtomicFieldUpdater();

        System.out.println(results);
        assertEquals(results.getFirst(), results.getSecond());
    }

    @Test
    public void testWithAtomicInteger() throws InterruptedException {
        AtomicFieldUpdater atomicFieldUpdater = new AtomicFieldUpdater();

        Pair<Integer, Integer> results = atomicFieldUpdater.runWithAtomicInteger();

        System.out.println(results);
        assertEquals(results.getFirst(), results.getSecond());
    }
}