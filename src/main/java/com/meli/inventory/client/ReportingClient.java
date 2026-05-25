package com.meli.inventory.client;

import com.meli.inventory.dto.StockMetricsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ReportingClient {

    private final RestTemplate restTemplate;

    @Value("${reporting.service.url}")
    private String reportingServiceUrl;

    public ReportingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendMetrics(StockMetricsResponse metrics) {
        try {
            restTemplate.postForObject(reportingServiceUrl + "/api/reports/stock", metrics, Void.class);
        } catch (Exception e) {
            // silently ignores connectivity failures to reporting-service
        }
    }
}
