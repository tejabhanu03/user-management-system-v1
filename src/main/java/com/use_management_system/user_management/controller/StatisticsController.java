package com.use_management_system.user_management.controller;

import com.use_management_system.user_management.dto.StatisticsResponse;
import com.use_management_system.user_management.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/overview")
    public ResponseEntity<StatisticsResponse> getOverviewStatistics() {
        return ResponseEntity.ok(statisticsService.getOverviewStatistics());
    }
}
