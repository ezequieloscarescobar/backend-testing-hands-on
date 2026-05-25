package com.meli.inventory.dto;

public record StockMetricsResponse(
        Integer outOfStockProducts,
        Integer activeReservations,
        Integer totalAvailableUnits
) {}
