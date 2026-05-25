package com.meli.inventory.controller;

import com.meli.inventory.dto.*;
import com.meli.inventory.service.InventoryService;
import com.meli.inventory.validator.StockOperationValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final StockOperationValidator validator;

    public InventoryController(InventoryService inventoryService, StockOperationValidator validator) {
        this.inventoryService = inventoryService;
        this.validator = validator;
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@RequestBody ReserveRequest request) {
        try {
            validator.validateReserveRequest(request);
            ReservationResponse response = inventoryService.reserveStock(request);
            return ResponseEntity.status(201).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody ConfirmRequest request) {
        try {
            validator.validateConfirmRequest(request);
            ReservationResponse response = inventoryService.confirmReservation(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/release")
    public ResponseEntity<?> release(@RequestBody ReleaseRequest request) {
        try {
            validator.validateReleaseRequest(request);
            ReservationResponse response = inventoryService.releaseReservation(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/availability/{productId}")
    public ResponseEntity<?> availability(@PathVariable String productId) {
        try {
            AvailabilityResponse response = inventoryService.getAvailability(productId);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}
