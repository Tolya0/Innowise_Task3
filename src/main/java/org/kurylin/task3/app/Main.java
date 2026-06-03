package org.kurylin.task3.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kurylin.task3.config.AppConfig;
import org.kurylin.task3.config.CarInit;
import org.kurylin.task3.config.ConfigLoader;
import org.kurylin.task3.domain.BoxPool;
import org.kurylin.task3.domain.PartsWarehouse;
import org.kurylin.task3.entity.Car;
import org.kurylin.task3.task.SupplierTask;

import org.kurylin.task3.exception.ConfigurationException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, ConfigurationException {
        AppConfig config = ConfigLoader.load();
        log.info("Configuration loaded: {} boxes, {} cars",
                config.boxes(), config.carInits().size());

        PartsWarehouse warehouse = PartsWarehouse.getInstance();
        warehouse.init(config.initialStock());

        BoxPool boxPool = new BoxPool(config.boxes());

        SupplierTask supplierTask = new SupplierTask(
                warehouse,
                config.supplierConfig().intervalMs(),
                config.supplierConfig().cycles(),
                config.supplierConfig().batchMap()
        );
        ExecutorService supplierExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "Supplier");
            t.setDaemon(true);
            return t;
        });
        supplierExecutor.submit(supplierTask);

        List<Car> cars = new ArrayList<>();
        for (CarInit ci : config.carInits()) {
            cars.add(new Car(ci.id(), ci.name(), ci.order(), ci.repairMs(), boxPool, warehouse));
        }

        ExecutorService carExecutor = Executors.newFixedThreadPool(cars.size(), r -> {
            Thread t = new Thread(r);
            t.setDaemon(false);
            return t;
        });
        for (Car car : cars) {
            carExecutor.submit(car);
        }

        carExecutor.shutdown();
        boolean finished = carExecutor.awaitTermination(60, TimeUnit.SECONDS);
        if (finished) {
            log.info("All cars have been serviced. Shutting down supplier.");
        } else {
            log.warn("Timeout: not all cars finished within 60 seconds.");
        }

        supplierExecutor.shutdownNow();
        supplierExecutor.awaitTermination(5, TimeUnit.SECONDS);

        log.info("Autoservice shut down.");
    }
}
