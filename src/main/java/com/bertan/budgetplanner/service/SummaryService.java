package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.dto.BalanceResponse;
import com.bertan.budgetplanner.dto.CategoriesSummaryResponse;
import com.bertan.budgetplanner.dto.MonthlySummaryResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class SummaryService {

    private final TransactionService transactionService;

    public SummaryService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public BalanceResponse getBalanceSummary() {

        BigDecimal totalIncome = transactionService.getTotalIncome();
        BigDecimal totalExpense = transactionService.getTotalExpense();
        BigDecimal netBalance = totalIncome.subtract(totalExpense)
                .setScale(2, RoundingMode.HALF_UP);

        return new BalanceResponse(netBalance);
    }

    public MonthlySummaryResponse getMonthlySummary(int month, int year) {

        BigDecimal monthlyIncome = transactionService.getMonthlyIncome(month, year)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyExpense = transactionService.getMonthlyExpense(month, year)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyNetBalance = monthlyIncome.subtract(monthlyExpense)
                .setScale(2, RoundingMode.HALF_UP);

        return new MonthlySummaryResponse(monthlyIncome, monthlyExpense, monthlyNetBalance);
    }

    public List<CategoriesSummaryResponse> getCategoriesSummary(int month, int year) {

        List<CategoriesSummaryResponse> categoriesSummaries = transactionService.getCategoriesSummaries(month, year);

        return categoriesSummaries.stream()
                .map(category -> new CategoriesSummaryResponse(
                        category.category(),
                        category.totalIncome().setScale(2, RoundingMode.HALF_UP),
                        category.totalExpense().setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();
    }
}
