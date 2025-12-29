package com.bertan.budgetplanner.service.impl;

import com.bertan.budgetplanner.dto.TransactionResponseDTO;
import com.bertan.budgetplanner.mapper.TransactionMapper;
import com.bertan.budgetplanner.repository.TransactionRepository;
import com.bertan.budgetplanner.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponseDTO> findAll(Pageable pageable) {

        return transactionRepository.findAll(pageable)
                .map(transactionMapper::toDto);
    }
}
