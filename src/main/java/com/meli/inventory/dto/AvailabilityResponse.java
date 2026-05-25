package com.meli.inventory.dto;

public record AvailabilityResponse(String productId, Integer available, Integer reserved) {}
