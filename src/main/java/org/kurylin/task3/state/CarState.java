package org.kurylin.task3.state;

import org.kurylin.task3.entity.Car;
import org.kurylin.task3.exception.ServiceException;

public interface CarState {

    void next(Car car) throws ServiceException, InterruptedException;
}
