package org.kurylin.task3.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kurylin.task3.domain.BoxPool;
import org.kurylin.task3.domain.PartsWarehouse;
import org.kurylin.task3.domain.RepairOrder;
import org.kurylin.task3.exception.ServiceException;
import org.kurylin.task3.state.CarState;
import org.kurylin.task3.state.impl.ArrivedState;
import org.kurylin.task3.state.impl.LeavingState;

public class Car implements Runnable {

    private static final Logger log = LogManager.getLogger(Car.class);

    private final int id;
    private final String name;
    private final RepairOrder order;
    private final long repairMs;

    private CarState state;

    private final BoxPool boxPool;
    private final PartsWarehouse warehouse;

    public Car(int id,
               String name,
               RepairOrder order,
               long repairMs,
               BoxPool boxPool,
               PartsWarehouse warehouse) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.repairMs = repairMs;
        this.boxPool = boxPool;
        this.warehouse = warehouse;
        this.state = new ArrivedState();
    }

    @Override
    public void run() {
        try {
            while (!(state instanceof LeavingState)) {
                state.next(this);
            }
            state.next(this);
        } catch (InterruptedException e) {
            log.error("[{}] interrupted — stopping", name);
            Thread.currentThread().interrupt();
        } catch (ServiceException e) {
            log.error("[{}] service error: {}", name, e.getMessage(), e);
        }
        log.info("[{}] thread finished", name);
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public RepairOrder getOrder() { return order; }

    public long getRepairMs() { return repairMs; }

    public CarState getState() { return state; }

    public void setState(CarState state) { this.state = state; }

    public BoxPool getBoxPool() { return boxPool; }

    public PartsWarehouse getWarehouse() { return warehouse; }
}
