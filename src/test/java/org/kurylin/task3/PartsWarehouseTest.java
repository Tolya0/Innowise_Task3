package org.kurylin.task3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kurylin.task3.domain.PartType;
import org.kurylin.task3.domain.PartsWarehouse;
import org.kurylin.task3.domain.RepairOrder;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartsWarehouseTest {

    private PartsWarehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouse = PartsWarehouse.getInstance();
        Map<PartType, Integer> empty = new EnumMap<>(PartType.class);
        for (PartType pt : PartType.values()) {
            empty.put(pt, 0);
        }
        warehouse.init(empty);
    }

    @Test
    @Timeout(5)
    void takeBlocking_blocksUntilPartsAvailable() throws InterruptedException {
        RepairOrder order = new RepairOrder(Map.of(PartType.ENGINE, 1));

        CountDownLatch tookParts = new CountDownLatch(1);
        AtomicBoolean tookAfterSupply = new AtomicBoolean(false);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                warehouse.takeBlocking(order);
                tookAfterSupply.set(true);
                tookParts.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        TimeUnit.MILLISECONDS.sleep(100);
        assertFalse(tookAfterSupply.get(), "Car should be blocked — warehouse is empty");

        warehouse.putBatch(Map.of(PartType.ENGINE, 2));

        boolean done = tookParts.await(3, TimeUnit.SECONDS);
        assertTrue(done, "takeBlocking should have unblocked after putBatch");
        assertTrue(tookAfterSupply.get());

        executor.shutdownNow();
    }

    @Test
    @Timeout(5)
    void takeBlocking_deductsStock() throws InterruptedException {
        Map<PartType, Integer> initial = new EnumMap<>(PartType.class);
        initial.put(PartType.WHEEL, 4);
        warehouse.init(initial);

        RepairOrder order = new RepairOrder(Map.of(PartType.WHEEL, 4));
        warehouse.takeBlocking(order);

        Map<PartType, Integer> snap = warehouse.getStockSnapshot();
        assertTrue(snap.getOrDefault(PartType.WHEEL, 0) == 0, "All wheels should have been taken");
    }
}
