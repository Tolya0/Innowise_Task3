package org.kurylin.task3.state.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kurylin.task3.entity.Car;
import org.kurylin.task3.state.CarState;

public class ReadyState implements CarState {

    private static final Logger log = LogManager.getLogger(ReadyState.class);

    @Override
    public void next(Car car) throws InterruptedException {
        log.info("[{}] ready — releasing box", car.getName());
        car.getBoxPool().release(car.getId());
        car.setState(new LeavingState());
    }
}
