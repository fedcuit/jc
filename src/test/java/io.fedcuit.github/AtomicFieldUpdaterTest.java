package io.fedcuit.github;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class AtomicFieldUpdaterTest {
    @Test
    public void testWithIntrinsicLock() throws InterruptedException {
        AtomicFieldUpdater atomicFieldUpdater = new AtomicFieldUpdater();

        List<Integer> results = atomicFieldUpdater.runWithIntrinsicLock();

        System.out.println(results);
        assertEquals(results.get(0), results.get(1));
    }

    @Test
    public void testWithReentrantLock() throws InterruptedException {
        AtomicFieldUpdater atomicFieldUpdater = new AtomicFieldUpdater();

        List<Integer> results = atomicFieldUpdater.runWithReentrantLock();

        System.out.println(results);
        assertEquals(results.get(0), results.get(1));
    }

    @Test
    public void testWithAtomicFieldUpdater() throws InterruptedException, ExecutionException {
        AtomicFieldUpdater atomicFieldUpdater = new AtomicFieldUpdater();

        List<Integer> results = atomicFieldUpdater.runWithAtomicFieldUpdater();

        System.out.println(results);
        assertEquals(results.get(0), results.get(1));
    }

    @Test
    public void testWithAtomicInteger() throws InterruptedException {
        AtomicFieldUpdater atomicFieldUpdater = new AtomicFieldUpdater();

        List<Integer> results = atomicFieldUpdater.runWithAtomicInteger();

        System.out.println(results);
        assertEquals(results.get(0), results.get(1));
    }
}