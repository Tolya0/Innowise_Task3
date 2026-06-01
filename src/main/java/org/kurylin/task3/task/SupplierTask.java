package org.kurylin.task3.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kurylin.task3.domain.PartsWarehouse;
import org.kurylin.task3.domain.PartType;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SupplierTask implements Runnable {

    private static final Logger log = LogManager.getLogger(SupplierTask.class);

    private final PartsWarehouse warehouse;
    private final long intervalMs;
    private final int cycles;
    private final Map<PartType, Integer> batchMap;

    public SupplierTask(PartsWarehouse warehouse,
                        long intervalMs,
                        int cycles,
                        Map<PartType, Integer> batchMap) {
        this.warehouse = warehouse;
        this.intervalMs = intervalMs;
        this.cycles = cycles;
        this.batchMap = batchMap;
    }

    @Override
    public void run() {
        log.info("Supplier started: {} cycles, interval {} ms, batch {}", cycles, intervalMs, batchMap);
        for (int i = 1; i <= cycles; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(intervalMs);
            } catch (InterruptedException e) {
                log.warn("Supplier interrupted at cycle {}", i);
                Thread.currentThread().interrupt();
                return;
            }
            warehouse.putBatch(batchMap);
            log.info("Supplier — cycle {}/{} delivered", i, cycles);
        }
        log.info("Supplier finished all {} cycles", cycles);
    }
}
