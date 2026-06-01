package org.kurylin.task3.config;

import org.kurylin.task3.domain.PartType;

import java.util.List;
import java.util.Map;

public record AppConfig(
        int boxes,
        Map<PartType, Integer> initialStock,
        SupplierConfig supplierConfig,
        List<CarInit> carInits
) {}
