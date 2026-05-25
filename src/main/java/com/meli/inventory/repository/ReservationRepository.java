package com.meli.inventory.repository;

import com.meli.inventory.model.Reservation;
import com.meli.inventory.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    long countByProductIdAndStatus(String productId, ReservationStatus status);

    long countByStatus(ReservationStatus status);

    List<Reservation> findByStatus(ReservationStatus status);
}
