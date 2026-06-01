package org.kurylin.task3.domain;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class RepairOrder {

    private final Map<PartType, Integer> parts;

    public RepairOrder(Map<PartType, Integer> parts) {
        this.parts = Collections.unmodifiableMap(new EnumMap<>(parts));
    }

    public Map<PartType, Integer> getParts() {
        return parts;
    }

    @Override
    public String toString() {
        return parts.toString();
    }
}
