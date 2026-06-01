package org.kurylin.task3.state.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kurylin.task3.entity.Car;
import org.kurylin.task3.exception.ServiceException;
import org.kurylin.task3.state.CarState;

public class ArrivedState implements CarState {

    private static final Logger log = LogManager.getLogger(ArrivedState.class);

    @Override
    public void next(Car car) throws ServiceException, InterruptedException {
        log.info("[{}] arrived (order: {})", car.getName(), car.getOrder());
        car.setState(new WaitingForPartsState());
    }
}
