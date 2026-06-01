package org.kurylin.task3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kurylin.task3.domain.BoxPool;
import org.kurylin.task3.domain.PartType;
import org.kurylin.task3.domain.PartsWarehouse;
import org.kurylin.task3.domain.RepairOrder;
import org.kurylin.task3.entity.Car;
import org.kurylin.task3.state.impl.LeavingState;
import org.kurylin.task3.task.SupplierTask;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class IntegrationTest {

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
    @Timeout(15)
    void bothCarsMustReachLeavingState() throws InterruptedException {
        BoxPool boxPool = new BoxPool(2);

        RepairOrder orderA = new RepairOrder(Map.of(PartType.ENGINE, 1));
        RepairOrder orderB = new RepairOrder(Map.of(PartType.ENGINE, 1));

        Car carA = new Car(1, "Car-A", orderA, 100, boxPool, warehouse);
        Car carB = new Car(2, "Car-B", orderB, 100, boxPool, warehouse);

        SupplierTask supplier = new SupplierTask(warehouse, 200, 5, Map.of(PartType.ENGINE, 1));

        ExecutorService supplierExec = Executors.newSingleThreadExecutor();
        ExecutorService carExec = Executors.newFixedThreadPool(2);

        supplierExec.submit(supplier);
        carExec.submit(carA);
        carExec.submit(carB);

        carExec.shutdown();
        carExec.awaitTermination(12, TimeUnit.SECONDS);
        supplierExec.shutdownNow();
        supplierExec.awaitTermination(2, TimeUnit.SECONDS);

        assertInstanceOf(LeavingState.class, carA.getState(), "Car-A should be in LeavingState");
        assertInstanceOf(LeavingState.class, carB.getState(), "Car-B should be in LeavingState");
    }
}
