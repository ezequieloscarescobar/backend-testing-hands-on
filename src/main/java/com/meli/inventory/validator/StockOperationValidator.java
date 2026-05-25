package com.meli.inventory.validator;

import com.meli.inventory.dto.ConfirmRequest;
import com.meli.inventory.dto.ReleaseRequest;
import com.meli.inventory.dto.ReserveRequest;
import org.springframework.stereotype.Component;

@Component
public class StockOperationValidator {

    public void validateReserveRequest(ReserveRequest request) {
        if (request.productId() == null || request.productId().isBlank()) {
            throw new IllegalArgumentException("productId es requerido");
        }
        if (request.orderId() == null || request.orderId().isBlank()) {
            throw new IllegalArgumentException("orderId es requerido");
        }
        if (request.units() == null || request.units() <= 0) {
            throw new IllegalArgumentException("units debe ser mayor a 0");
        }
    }

    public void validateConfirmRequest(ConfirmRequest request) {
        if (request.reservationId() == null || request.reservationId().isBlank()) {
            throw new IllegalArgumentException("reservationId es requerido");
        }
    }

    public void validateReleaseRequest(ReleaseRequest request) {
        if (request.reservationId() == null || request.reservationId().isBlank()) {
            throw new IllegalArgumentException("reservationId es requerido");
        }
    }
}
