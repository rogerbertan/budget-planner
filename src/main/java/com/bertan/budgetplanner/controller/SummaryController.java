package com.bertan.budgetplanner.controller;

import com.bertan.budgetplanner.dto.BalanceResponse;
import com.bertan.budgetplanner.dto.CategoriesSummaryResponse;
import com.bertan.budgetplanner.dto.MonthlySummaryResponse;
import com.bertan.budgetplanner.service.SummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/summary")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalanceSummary() {
        BalanceResponse balance = summaryService.getBalanceSummary();
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @RequestParam("month") int month,
            @RequestParam("year") int year) {

        MonthlySummaryResponse monthlySummary = summaryService.getMonthlySummary(month, year);
        return ResponseEntity.ok(monthlySummary);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoriesSummaryResponse>> getCategoriesSummary(
            @RequestParam("month") int month,
            @RequestParam("year") int year) {

        List<CategoriesSummaryResponse> categoriesSummary = summaryService.getCategoriesSummary(month, year);
        return ResponseEntity.ok(categoriesSummary);
    }
}
