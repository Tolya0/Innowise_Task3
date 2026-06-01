package org.kurylin.task3.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PartsWarehouse {

    private static final Logger log = LogManager.getLogger(PartsWarehouse.class);

    private static final PartsWarehouse INSTANCE = new PartsWarehouse();

    public static PartsWarehouse getInstance() {
        return INSTANCE;
    }

    private PartsWarehouse() {}

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition partsAvailable = lock.newCondition();
    private final Map<PartType, Integer> stock = new EnumMap<>(PartType.class);

    public void init(Map<PartType, Integer> initial) {
        lock.lock();
        try {
            stock.clear();
            stock.putAll(initial);
            log.info("Warehouse initialised: {}", stock);
        } finally {
            lock.unlock();
        }
    }

    public void takeBlocking(RepairOrder order) throws InterruptedException {
        lock.lock();
        try {
            while (!hasEnough(order)) {
                log.debug("Not enough parts for order {}; awaiting supply", order);
                partsAvailable.await();
            }
            for (Map.Entry<PartType, Integer> e : order.getParts().entrySet()) {
                stock.merge(e.getKey(), -e.getValue(), Integer::sum);
            }
            log.debug("Parts taken for order {}; stock now: {}", order, stock);
        } finally {
            lock.unlock();
        }
    }

    public void putBatch(Map<PartType, Integer> batch) {
        lock.lock();
        try {
            for (Map.Entry<PartType, Integer> e : batch.entrySet()) {
                stock.merge(e.getKey(), e.getValue(), Integer::sum);
            }
            log.info("Supplier delivered batch {}; stock now: {}", batch, stock);
            partsAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean hasEnough(RepairOrder order) {
        for (Map.Entry<PartType, Integer> e : order.getParts().entrySet()) {
            int available = stock.getOrDefault(e.getKey(), 0);
            if (available < e.getValue()) {
                return false;
            }
        }
        return true;
    }

    public Map<PartType, Integer> getStockSnapshot() {
        lock.lock();
        try {
            return new EnumMap<>(stock);
        } finally {
            lock.unlock();
        }
    }
}
