package com.bertan.budgetplanner.service;


import com.bertan.budgetplanner.dto.TransactionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    Page<TransactionResponseDTO> findAll(Pageable pageable);
}
