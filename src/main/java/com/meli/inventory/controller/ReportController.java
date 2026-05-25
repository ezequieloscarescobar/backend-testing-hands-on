package com.meli.inventory.controller;

import com.meli.inventory.dto.StockMetricsResponse;
import com.meli.inventory.report.StockReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory/reports")
public class ReportController {

    private final StockReportService stockReportService;

    public ReportController(StockReportService stockReportService) {
        this.stockReportService = stockReportService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<StockMetricsResponse> metrics() {
        return ResponseEntity.ok(stockReportService.getMetrics());
    }
}
