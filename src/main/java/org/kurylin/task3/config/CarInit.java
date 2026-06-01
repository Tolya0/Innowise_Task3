package org.kurylin.task3.config;

import org.kurylin.task3.domain.RepairOrder;

public record CarInit(
        int id,
        String name,
        long repairMs,
        RepairOrder order
) {}
