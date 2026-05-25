package com.meli.inventory.dto;

import java.time.LocalDateTime;

public record ReservationResponse(
        String reservationId,
        String productId,
        Integer units,
        String status,
        LocalDateTime expiresAt
) {}
