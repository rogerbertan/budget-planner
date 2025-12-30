package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.dto.BalanceResponseDTO;
import com.bertan.budgetplanner.dto.CategoriesSummaryResponseDTO;
import com.bertan.budgetplanner.dto.MonthlySummaryResponseDTO;

import java.util.List;

public interface SummaryService {

    BalanceResponseDTO getBalanceSummary();

    MonthlySummaryResponseDTO getMonthlySummary(int month, int year);

    List<CategoriesSummaryResponseDTO> getCategoriesSummary(int month, int year);
}
