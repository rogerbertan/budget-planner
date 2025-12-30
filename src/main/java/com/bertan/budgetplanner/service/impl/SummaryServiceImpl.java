package com.bertan.budgetplanner.service.impl;

import com.bertan.budgetplanner.dto.BalanceResponseDTO;
import com.bertan.budgetplanner.dto.CategoriesSummaryResponseDTO;
import com.bertan.budgetplanner.dto.MonthlySummaryResponseDTO;
import com.bertan.budgetplanner.service.SummaryService;
import com.bertan.budgetplanner.service.TransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class SummaryServiceImpl implements SummaryService {

    private final TransactionService transactionService;

    public SummaryServiceImpl(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public BalanceResponseDTO getBalanceSummary() {

        BigDecimal totalIncome = transactionService.getTotalIncome();
        BigDecimal totalExpense = transactionService.getTotalExpense();
        BigDecimal netBalance = totalIncome.subtract(totalExpense)
                .setScale(2, RoundingMode.HALF_UP);

        return new BalanceResponseDTO(netBalance);
    }

    @Override
    public MonthlySummaryResponseDTO getMonthlySummary(int month, int year) {

        BigDecimal monthlyIncome = transactionService.getMonthlyIncome(month, year)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyExpense = transactionService.getMonthlyExpense(month, year)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyNetBalance = monthlyIncome.subtract(monthlyExpense)
                .setScale(2, RoundingMode.HALF_UP);

        return new MonthlySummaryResponseDTO(monthlyIncome, monthlyExpense, monthlyNetBalance);
    }

    @Override
    public List<CategoriesSummaryResponseDTO> getCategoriesSummary(int month, int year) {

        List<CategoriesSummaryResponseDTO> categoriesSummaries = transactionService.getCategoriesSummaries(month, year);

        return categoriesSummaries.stream()
                .map(category -> new CategoriesSummaryResponseDTO(
                        category.category(),
                        category.totalIncome().setScale(2, RoundingMode.HALF_UP),
                        category.totalExpense().setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();
    }
}
