package org.kurylin.task3.state;

import org.kurylin.task3.entity.Car;

public interface CarState {

    void next(Car car) throws InterruptedException;
}
