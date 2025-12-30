package com.bertan.budgetplanner.controller;

import com.bertan.budgetplanner.dto.BalanceResponseDTO;
import com.bertan.budgetplanner.service.SummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/summary")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponseDTO> getBalanceSummary() {
        BalanceResponseDTO balance = summaryService.getBalanceSummary();
        return ResponseEntity.ok(balance);
    }
}
