package org.kurylin.task3.config;

import org.kurylin.task3.domain.PartType;
import org.kurylin.task3.domain.RepairOrder;
import org.kurylin.task3.exception.ServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConfigLoader {

    private static final String DEFAULT_RESOURCE = "autoservice-config.properties";

    private ConfigLoader() {}

    public static AppConfig load() {
        return load(DEFAULT_RESOURCE);
    }

    public static AppConfig load(String resourceName) {
        Properties props = new Properties();
        try (InputStream is = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new ServiceException("Config resource not found: " + resourceName);
            }
            props.load(is);
        } catch (IOException e) {
            throw new ServiceException("Failed to load config: " + resourceName, e);
        }

        int boxes = Integer.parseInt(props.getProperty("boxes").trim());

        Map<PartType, Integer> initialStock = new EnumMap<>(PartType.class);
        for (PartType pt : PartType.values()) {
            String key = "warehouse." + pt.name();
            if (props.containsKey(key)) {
                initialStock.put(pt, Integer.parseInt(props.getProperty(key).trim()));
            }
        }

        long intervalMs = Long.parseLong(props.getProperty("supplier.interval.ms").trim());
        int cycles = Integer.parseInt(props.getProperty("supplier.cycles").trim());
        Map<PartType, Integer> batchMap = new EnumMap<>(PartType.class);
        for (PartType pt : PartType.values()) {
            String key = "supplier.batch." + pt.name();
            if (props.containsKey(key)) {
                batchMap.put(pt, Integer.parseInt(props.getProperty(key).trim()));
            }
        }
        SupplierConfig supplierConfig = new SupplierConfig(intervalMs, cycles, batchMap);

        List<CarInit> carInits = new ArrayList<>();
        int carIndex = 1;
        while (props.containsKey("car." + carIndex + ".name")) {
            String prefix = "car." + carIndex + ".";
            String name = props.getProperty(prefix + "name").trim();
            long repairMs = Long.parseLong(props.getProperty(prefix + "repair.ms").trim());
            String orderStr = props.getProperty(prefix + "order").trim();
            RepairOrder order = parseOrder(orderStr);
            carInits.add(new CarInit(carIndex, name, repairMs, order));
            carIndex++;
        }

        return new AppConfig(boxes, initialStock, supplierConfig, carInits);
    }

    private static RepairOrder parseOrder(String orderStr) {
        Map<PartType, Integer> parts = new EnumMap<>(PartType.class);
        for (String token : orderStr.split(",")) {
            String[] kv = token.trim().split(":");
            if (kv.length != 2) {
                throw new ServiceException("Invalid order token: " + token);
            }
            PartType pt = PartType.valueOf(kv[0].trim().toUpperCase());
            int qty = Integer.parseInt(kv[1].trim());
            parts.put(pt, qty);
        }
        return new RepairOrder(parts);
    }
}
