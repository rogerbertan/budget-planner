package com.bertan.budgetplanner.service;


import com.bertan.budgetplanner.dto.CategoriesSummaryResponseDTO;
import com.bertan.budgetplanner.dto.CreateTransactionRequestDTO;
import com.bertan.budgetplanner.dto.TransactionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    Page<TransactionResponseDTO> findAll(Pageable pageable);

    TransactionResponseDTO createTransaction(CreateTransactionRequestDTO requestDTO);

    TransactionResponseDTO getTransactionById(Long id);

    TransactionResponseDTO updateTransaction(Long id, CreateTransactionRequestDTO requestDTO);

    void deleteTransaction(Long id);

    BigDecimal getTotalIncome();

    BigDecimal getTotalExpense();

    BigDecimal getMonthlyIncome(int month, int year);

    BigDecimal getMonthlyExpense(int month, int year);

    List<CategoriesSummaryResponseDTO> getCategoriesSummaries(int month, int year);
}
