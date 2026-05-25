package com.meli.inventory.service;

import com.meli.inventory.dto.*;
import com.meli.inventory.mapper.ReservationMapper;
import com.meli.inventory.model.Product;
import com.meli.inventory.model.Reservation;
import com.meli.inventory.model.ReservationStatus;
import com.meli.inventory.repository.ProductRepository;
import com.meli.inventory.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    @Value("${inventory.reservation.expiration-seconds:900}")
    private long expirationSeconds;

    public InventoryService(ProductRepository productRepository,
                            ReservationRepository reservationRepository,
                            ReservationMapper reservationMapper) {
        this.productRepository = productRepository;
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
    }

    public ReservationResponse reserveStock(ReserveRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + request.productId()));

        if (request.units() > product.getAvailable()) {
            throw new IllegalStateException("stock insuficiente");
        }

        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setProductId(request.productId());
        reservation.setOrderId(request.orderId());
        reservation.setUnits(request.units());
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusSeconds(expirationSeconds));

        product.setAvailable(product.getAvailable() - request.units());
        productRepository.save(product);
        reservationRepository.save(reservation);

        return reservationMapper.toResponse(reservation);
    }

    public ReservationResponse confirmReservation(ConfirmRequest request) {
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new NoSuchElementException("Reserva no encontrada: " + request.reservationId()));

        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new IllegalStateException(
                    "La reserva no puede confirmarse en su estado actual: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        return reservationMapper.toResponse(reservation);
    }

    public ReservationResponse releaseReservation(ReleaseRequest request) {
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new NoSuchElementException("Reserva no encontrada: " + request.reservationId()));

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("No se puede liberar una reserva confirmada");
        }

        Product product = productRepository.findById(reservation.getProductId())
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + reservation.getProductId()));

        product.setAvailable(product.getAvailable() + reservation.getUnits());
        productRepository.save(product);

        reservation.setStatus(ReservationStatus.RELEASED);
        reservationRepository.save(reservation);

        return reservationMapper.toResponse(reservation);
    }

    public AvailabilityResponse getAvailability(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + productId));

        long reserved = reservationRepository.countByProductIdAndStatus(productId, ReservationStatus.RESERVED);

        return new AvailabilityResponse(productId, product.getAvailable(), (int) reserved);
    }
}
