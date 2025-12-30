package com.bertan.budgetplanner.service.impl;

import com.bertan.budgetplanner.dto.BalanceResponseDTO;
import com.bertan.budgetplanner.service.SummaryService;
import com.bertan.budgetplanner.service.TransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        return new BalanceResponseDTO(netBalance);
    }
}
