package org.kurylin.task3.state.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kurylin.task3.entity.Car;
import org.kurylin.task3.state.CarState;

import java.util.concurrent.TimeUnit;

public class RepairingState implements CarState {

    private static final Logger log = LogManager.getLogger(RepairingState.class);

    @Override
    public void next(Car car) throws InterruptedException {
        log.info("[{}] repairing (estimated {} ms)", car.getName(), car.getRepairMs());
        TimeUnit.MILLISECONDS.sleep(car.getRepairMs());
        log.info("[{}] repair finished", car.getName());
        car.setState(new ReadyState());
    }
}
