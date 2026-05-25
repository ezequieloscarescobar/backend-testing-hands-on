package com.meli.inventory.report;

import com.meli.inventory.dto.StockMetricsResponse;
import com.meli.inventory.model.ReservationStatus;
import com.meli.inventory.repository.ProductRepository;
import com.meli.inventory.repository.ReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class StockReportService {

    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;

    public StockReportService(ProductRepository productRepository,
                               ReservationRepository reservationRepository) {
        this.productRepository = productRepository;
        this.reservationRepository = reservationRepository;
    }

    public StockMetricsResponse getMetrics() {
        int outOfStock = (int) productRepository.countByAvailable(0);
        int activeReservations = (int) reservationRepository.countByStatus(ReservationStatus.RESERVED);
        Integer totalAvailable = productRepository.sumAvailable();

        return new StockMetricsResponse(
                outOfStock,
                activeReservations,
                totalAvailable != null ? totalAvailable : 0
        );
    }
}
