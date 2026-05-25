package com.meli.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meli.inventory.dto.*;
import com.meli.inventory.service.InventoryService;
import com.meli.inventory.validator.StockOperationValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private StockOperationValidator validator;

    @Test
    void reserveReturns201() throws Exception {
        ReservationResponse response = new ReservationResponse(
                "res-abc", "prod-1", 3, "RESERVED", LocalDateTime.now().plusMinutes(15));

        when(inventoryService.reserveStock(any())).thenReturn(response);

        String body = "{\"productId\":\"prod-1\",\"units\":3,\"orderId\":\"order-99\"}";

        mockMvc.perform(post("/api/inventory/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void confirmReturns200() throws Exception {
        ReservationResponse response = new ReservationResponse(
                "res-abc", "prod-1", 3, "CONFIRMED", LocalDateTime.now().plusMinutes(15));

        when(inventoryService.confirmReservation(any())).thenReturn(response);

        String body = "{\"reservationId\":\"res-abc\"}";

        mockMvc.perform(post("/api/inventory/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void reserveReturns409WhenStockInsuficiente() throws Exception {
        when(inventoryService.reserveStock(any()))
                .thenThrow(new IllegalStateException("stock insuficiente"));

        String body = "{\"productId\":\"prod-1\",\"units\":99,\"orderId\":\"order-99\"}";

        mockMvc.perform(post("/api/inventory/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(content().string("{\"error\":\"stock insuficiente\"}"));
    }

    @Test
    void releaseReturns200() throws Exception {
        ReservationResponse response = new ReservationResponse(
                "res-abc", "prod-1", 3, "RELEASED", LocalDateTime.now().plusMinutes(15));

        when(inventoryService.releaseReservation(any())).thenReturn(response);

        String body = "{\"reservationId\":\"res-abc\"}";

        mockMvc.perform(post("/api/inventory/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }
}
