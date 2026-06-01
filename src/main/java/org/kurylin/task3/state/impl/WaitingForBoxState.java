package org.kurylin.task3.state.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kurylin.task3.entity.Car;
import org.kurylin.task3.exception.ServiceException;
import org.kurylin.task3.state.CarState;

public class WaitingForBoxState implements CarState {

    private static final Logger log = LogManager.getLogger(WaitingForBoxState.class);

    @Override
    public void next(Car car) throws ServiceException, InterruptedException {
        log.info("[{}] waiting for a repair box", car.getName());
        car.getBoxPool().acquire(car.getId());
        log.info("[{}] box acquired", car.getName());
        car.setState(new RepairingState());
    }
}
