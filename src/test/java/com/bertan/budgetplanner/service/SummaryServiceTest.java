package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.dto.BalanceResponseDTO;
import com.bertan.budgetplanner.dto.CategoriesSummaryResponseDTO;
import com.bertan.budgetplanner.dto.MonthlySummaryResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private SummaryService summaryService;

    @Test
    void shouldCalculatePositiveBalance() {
        when(transactionService.getTotalIncome()).thenReturn(BigDecimal.valueOf(1000));
        when(transactionService.getTotalExpense()).thenReturn(BigDecimal.valueOf(400));

        BalanceResponseDTO result = summaryService.getBalanceSummary();

        assertThat(result.balance()).isEqualByComparingTo(BigDecimal.valueOf(600).setScale(2));
    }

    @Test
    void shouldCalculateNegativeBalanceWhenExpensesExceedIncome() {
        when(transactionService.getTotalIncome()).thenReturn(BigDecimal.valueOf(100));
        when(transactionService.getTotalExpense()).thenReturn(BigDecimal.valueOf(400));

        BalanceResponseDTO result = summaryService.getBalanceSummary();

        assertThat(result.balance()).isEqualByComparingTo(BigDecimal.valueOf(-300).setScale(2));
    }

    @Test
    void shouldCalculateZeroBalanceWhenNoTransactionsExist() {
        when(transactionService.getTotalIncome()).thenReturn(BigDecimal.ZERO);
        when(transactionService.getTotalExpense()).thenReturn(BigDecimal.ZERO);

        BalanceResponseDTO result = summaryService.getBalanceSummary();

        assertThat(result.balance()).isEqualByComparingTo(BigDecimal.ZERO.setScale(2));
    }

    @Test
    void shouldRoundBalanceToTwoDecimalPlaces() {
        when(transactionService.getTotalIncome()).thenReturn(new BigDecimal("100.005"));
        when(transactionService.getTotalExpense()).thenReturn(BigDecimal.ZERO);

        BalanceResponseDTO result = summaryService.getBalanceSummary();

        assertThat(result.balance()).isEqualByComparingTo(new BigDecimal("100.01"));
    }

    @Test
    void shouldCalculateMonthlySummary() {
        when(transactionService.getMonthlyIncome(6, 2026)).thenReturn(BigDecimal.valueOf(500));
        when(transactionService.getMonthlyExpense(6, 2026)).thenReturn(BigDecimal.valueOf(200));

        MonthlySummaryResponseDTO result = summaryService.getMonthlySummary(6, 2026);

        assertThat(result.totalIncome()).isEqualByComparingTo(BigDecimal.valueOf(500).setScale(2));
        assertThat(result.totalExpense()).isEqualByComparingTo(BigDecimal.valueOf(200).setScale(2));
        assertThat(result.netBalance()).isEqualByComparingTo(BigDecimal.valueOf(300).setScale(2));
    }

    @Test
    void shouldCalculateMonthlySummaryWithNoActivity() {
        when(transactionService.getMonthlyIncome(1, 2026)).thenReturn(BigDecimal.ZERO);
        when(transactionService.getMonthlyExpense(1, 2026)).thenReturn(BigDecimal.ZERO);

        MonthlySummaryResponseDTO result = summaryService.getMonthlySummary(1, 2026);

        assertThat(result.totalIncome()).isEqualByComparingTo(BigDecimal.ZERO.setScale(2));
        assertThat(result.totalExpense()).isEqualByComparingTo(BigDecimal.ZERO.setScale(2));
        assertThat(result.netBalance()).isEqualByComparingTo(BigDecimal.ZERO.setScale(2));
    }

    @Test
    void shouldReturnCategoriesSummaryRoundedToTwoDecimals() {
        CategoriesSummaryResponseDTO raw = new CategoriesSummaryResponseDTO(
                "Food", new BigDecimal("100.005"), new BigDecimal("50.001"));
        when(transactionService.getCategoriesSummaries(6, 2026)).thenReturn(List.of(raw));

        List<CategoriesSummaryResponseDTO> result = summaryService.getCategoriesSummary(6, 2026);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).category()).isEqualTo("Food");
        assertThat(result.get(0).totalIncome()).isEqualByComparingTo(new BigDecimal("100.01"));
        assertThat(result.get(0).totalExpense()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void shouldReturnEmptyCategoriesSummaryWhenNoneExist() {
        when(transactionService.getCategoriesSummaries(6, 2026)).thenReturn(List.of());

        List<CategoriesSummaryResponseDTO> result = summaryService.getCategoriesSummary(6, 2026);

        assertThat(result).isEmpty();
    }
}