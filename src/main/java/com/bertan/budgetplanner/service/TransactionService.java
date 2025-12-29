package com.bertan.budgetplanner.service;


import com.bertan.budgetplanner.dto.CreateTransactionRequestDTO;
import com.bertan.budgetplanner.dto.TransactionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    Page<TransactionResponseDTO> findAll(Pageable pageable);

    TransactionResponseDTO createTransaction(CreateTransactionRequestDTO requestDTO);

    TransactionResponseDTO getTransactionById(Long id);

    TransactionResponseDTO updateTransaction(Long id, CreateTransactionRequestDTO requestDTO);
}
