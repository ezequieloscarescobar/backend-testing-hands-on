package com.meli.inventory.mapper;

import com.meli.inventory.dto.ReservationResponse;
import com.meli.inventory.model.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getProductId(),
                reservation.getUnits(),
                reservation.getStatus().name(),
                reservation.getExpiresAt()
        );
    }
}
