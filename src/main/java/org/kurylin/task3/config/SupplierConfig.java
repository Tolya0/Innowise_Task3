package org.kurylin.task3.config;

import org.kurylin.task3.domain.PartType;

import java.util.Map;

public record SupplierConfig(
        long intervalMs,
        int cycles,
        Map<PartType, Integer> batchMap
) {}
