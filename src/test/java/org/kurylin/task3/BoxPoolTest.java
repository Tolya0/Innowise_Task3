package org.kurylin.task3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kurylin.task3.domain.BoxPool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoxPoolTest {

    @Test
    @Timeout(10)
    void maxConcurrentCarsNeverExceedsCapacity() throws InterruptedException {
        BoxPool pool = new BoxPool(1);
        int carCount = 3;

        AtomicInteger current = new AtomicInteger(0);
        AtomicInteger maxConcurrent = new AtomicInteger(0);
        CountDownLatch allDone = new CountDownLatch(carCount);

        ExecutorService executor = Executors.newFixedThreadPool(carCount);

        for (int i = 0; i < carCount; i++) {
            final int carId = i + 1;
            executor.submit(() -> {
                try {
                    pool.acquire(carId);
                    int now = current.incrementAndGet();
                    maxConcurrent.updateAndGet(prev -> Math.max(prev, now));
                    TimeUnit.MILLISECONDS.sleep(50);
                    current.decrementAndGet();
                    pool.release(carId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    allDone.countDown();
                }
            });
        }

        boolean done = allDone.await(8, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertEquals(true, done, "All cars should have finished");
        assertEquals(1, maxConcurrent.get(), "Max concurrent cars in box must equal pool capacity (1)");
    }
}
