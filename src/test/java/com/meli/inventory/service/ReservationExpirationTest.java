package com.meli.inventory.service;

import com.meli.inventory.model.Reservation;
import com.meli.inventory.model.ReservationStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationExpirationTest {

    @Test
    void reservationShouldBeExpiredAfterTimeout() throws InterruptedException {
        Reservation reservation = new Reservation();
        reservation.setId("res-exp-1");
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setExpiresAt(LocalDateTime.now().plusSeconds(1));

        Thread.sleep(1500);

        boolean isExpired = LocalDateTime.now().isAfter(reservation.getExpiresAt());

        assertThat(isExpired).isTrue();
    }

    @Test
    void activeReservationShouldNotBeExpired() {
        Reservation reservation = new Reservation();
        reservation.setId("res-active-1");
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        boolean isExpired = LocalDateTime.now().isAfter(reservation.getExpiresAt());

        assertThat(isExpired).isFalse();
    }
}
