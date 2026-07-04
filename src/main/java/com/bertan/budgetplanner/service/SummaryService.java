package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.config.JWTUserData;
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

    public BalanceResponse getBalanceSummary(JWTUserData principal) {

        BigDecimal totalIncome = transactionService.getTotalIncome(principal);
        BigDecimal totalExpense = transactionService.getTotalExpense(principal);
        BigDecimal netBalance = totalIncome.subtract(totalExpense)
                .setScale(2, RoundingMode.HALF_UP);

        return new BalanceResponse(netBalance);
    }

    public MonthlySummaryResponse getMonthlySummary(int month, int year, JWTUserData principal) {

        BigDecimal monthlyIncome = transactionService.getMonthlyIncome(month, year, principal)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyExpense = transactionService.getMonthlyExpense(month, year, principal)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyNetBalance = monthlyIncome.subtract(monthlyExpense)
                .setScale(2, RoundingMode.HALF_UP);

        return new MonthlySummaryResponse(monthlyIncome, monthlyExpense, monthlyNetBalance);
    }

    public List<CategoriesSummaryResponse> getCategoriesSummary(int month, int year, JWTUserData principal) {

        List<CategoriesSummaryResponse> categoriesSummaries = transactionService.getCategoriesSummaries(month, year, principal);

        return categoriesSummaries.stream()
                .map(category -> new CategoriesSummaryResponse(
                        category.category(),
                        category.totalIncome().setScale(2, RoundingMode.HALF_UP),
                        category.totalExpense().setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();
    }
}
