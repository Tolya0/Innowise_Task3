package org.kurylin.task3.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Semaphore;

public class BoxPool {

    private static final Logger log = LogManager.getLogger(BoxPool.class);

    private final Semaphore semaphore;
    private final int capacity;

    public BoxPool(int boxes) {
        this.capacity = boxes;
        this.semaphore = new Semaphore(boxes, true);
    }

    public void acquire(int carId) throws InterruptedException {
        log.debug("Car #{} trying to acquire a box (available: {})", carId, semaphore.availablePermits());
        semaphore.acquire();
        log.debug("Car #{} acquired a box", carId);
    }

    public void release(int carId) {
        semaphore.release();
        log.debug("Car #{} released a box (available: {})", carId, semaphore.availablePermits());
    }

    public int availablePermits() {
        return semaphore.availablePermits();
    }

    public int getCapacity() {
        return capacity;
    }
}
