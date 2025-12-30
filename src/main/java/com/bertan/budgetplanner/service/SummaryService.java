package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.dto.BalanceResponseDTO;
import com.bertan.budgetplanner.dto.MonthlySummaryResponseDTO;

public interface SummaryService {

    BalanceResponseDTO getBalanceSummary();

    MonthlySummaryResponseDTO getMonthlySummary(int month, int year);
}
