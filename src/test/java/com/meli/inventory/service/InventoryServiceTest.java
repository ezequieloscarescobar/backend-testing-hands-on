package com.meli.inventory.service;

import com.meli.inventory.dto.*;
import com.meli.inventory.mapper.ReservationMapper;
import com.meli.inventory.model.Product;
import com.meli.inventory.model.Reservation;
import com.meli.inventory.model.ReservationStatus;
import com.meli.inventory.repository.ProductRepository;
import com.meli.inventory.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private InventoryService inventoryService;

    private Product product;
    private Reservation reservation;
    private ReservationResponse reservationResponse;

    @BeforeEach
    void setUp() {
        product = new Product("prod-1", "Notebook Pro", 10);

        reservation = new Reservation();
        reservation.setId("res-abc");
        reservation.setProductId("prod-1");
        reservation.setOrderId("order-99");
        reservation.setUnits(3);
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        reservationResponse = new ReservationResponse("res-abc", "prod-1", 3, "RESERVED", reservation.getExpiresAt());
    }

    @Test
    void testReserve() {
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(product));
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(reservationMapper.toResponse(any())).thenReturn(reservationResponse);
        lenient().when(productRepository.save(any())).thenReturn(product);

        ReserveRequest request = new ReserveRequest("prod-1", 3, "order-99");
        ReservationResponse response = inventoryService.reserveStock(request);

        assertThat(response).isNotNull();
        verify(reservationRepository, times(1)).save(any());
        verify(reservationMapper, times(1)).toResponse(any());
    }

    @Test
    void test1() {
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(product));
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(reservationMapper.toResponse(any())).thenReturn(reservationResponse);
        lenient().when(productRepository.findById("prod-2")).thenReturn(Optional.empty());

        ReserveRequest request = new ReserveRequest("prod-1", 5, "order-1");
        ReservationResponse response = inventoryService.reserveStock(request);

        assertThat(response).isNotNull();
    }

    @Test
    void testOk() {
        reservation.setStatus(ReservationStatus.RESERVED);
        when(reservationRepository.findById("res-abc")).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenReturn(reservation);

        ReservationResponse confirmedResponse = new ReservationResponse(
                "res-abc", "prod-1", 3, "CONFIRMED", reservation.getExpiresAt());
        when(reservationMapper.toResponse(any())).thenReturn(confirmedResponse);

        ConfirmRequest request = new ConfirmRequest("res-abc");
        ReservationResponse response = inventoryService.confirmReservation(request);

        assertThat(response).isNotNull();
        verify(reservationRepository, times(1)).save(any());
    }

    @Test
    void testRelease() {
        reservation.setStatus(ReservationStatus.RESERVED);
        when(reservationRepository.findById("res-abc")).thenReturn(Optional.of(reservation));
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(product));
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(productRepository.save(any())).thenReturn(product);

        ReservationResponse releasedResponse = new ReservationResponse(
                "res-abc", "prod-1", 3, "RELEASED", reservation.getExpiresAt());
        when(reservationMapper.toResponse(any())).thenReturn(releasedResponse);

        ReleaseRequest request = new ReleaseRequest("res-abc");
        ReservationResponse response = inventoryService.releaseReservation(request);

        assertThat(response).isNotNull();
        verify(productRepository, times(1)).save(any());
        verify(reservationRepository, times(1)).save(any());
    }

    @Test
    void testAvailability() {
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(product));
        when(reservationRepository.countByProductIdAndStatus("prod-1", ReservationStatus.RESERVED)).thenReturn(2L);

        AvailabilityResponse response = inventoryService.getAvailability("prod-1");

        assertThat(response).isNotNull();
    }
}
