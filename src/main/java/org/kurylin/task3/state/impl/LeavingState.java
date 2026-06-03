package org.kurylin.task3.state.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kurylin.task3.entity.Car;
import org.kurylin.task3.state.CarState;

public class LeavingState implements CarState {

    private static final Logger log = LogManager.getLogger(LeavingState.class);

    @Override
    public void next(Car car) throws InterruptedException {
        log.info("[{}] leaving the service — done", car.getName());
    }
}
