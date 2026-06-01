package org.kurylin.task3.state.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kurylin.task3.entity.Car;
import org.kurylin.task3.exception.ServiceException;
import org.kurylin.task3.state.CarState;

public class WaitingForPartsState implements CarState {

    private static final Logger log = LogManager.getLogger(WaitingForPartsState.class);

    @Override
    public void next(Car car) throws ServiceException, InterruptedException {
        log.info("[{}] waiting for parts {}", car.getName(), car.getOrder());
        car.getWarehouse().takeBlocking(car.getOrder());
        log.info("[{}] parts received", car.getName());
        car.setState(new WaitingForBoxState());
    }
}
