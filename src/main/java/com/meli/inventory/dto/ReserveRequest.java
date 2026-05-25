package com.meli.inventory.dto;

public record ReserveRequest(String productId, Integer units, String orderId) {}
