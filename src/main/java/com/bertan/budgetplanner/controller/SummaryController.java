package com.bertan.budgetplanner.controller;

import com.bertan.budgetplanner.dto.BalanceResponseDTO;
import com.bertan.budgetplanner.dto.CategoriesSummaryResponseDTO;
import com.bertan.budgetplanner.dto.MonthlySummaryResponseDTO;
import com.bertan.budgetplanner.service.SummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

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

    @GetMapping("/monthly")
    public ResponseEntity<MonthlySummaryResponseDTO> getMonthlySummary(
            @RequestParam("month") int month,
            @RequestParam("year") int year) {

        MonthlySummaryResponseDTO monthlySummary = summaryService.getMonthlySummary(month, year);
        return ResponseEntity.ok(monthlySummary);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoriesSummaryResponseDTO>> getCategoriesSummary(
            @RequestParam("month") int month,
            @RequestParam("year") int year) {

        List<CategoriesSummaryResponseDTO> categoriesSummary = summaryService.getCategoriesSummary(month, year);
        return ResponseEntity.ok(categoriesSummary);
    }
}
