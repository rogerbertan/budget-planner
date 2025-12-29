package com.bertan.budgetplanner.service.impl;

import com.bertan.budgetplanner.domain.Category;
import com.bertan.budgetplanner.domain.Transaction;
import com.bertan.budgetplanner.dto.CreateTransactionRequestDTO;
import com.bertan.budgetplanner.dto.TransactionResponseDTO;
import com.bertan.budgetplanner.mapper.TransactionMapper;
import com.bertan.budgetplanner.repository.CategoryRepository;
import com.bertan.budgetplanner.repository.TransactionRepository;
import com.bertan.budgetplanner.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  CategoryRepository categoryRepository,
                                  TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponseDTO> findAll(Pageable pageable) {

        return transactionRepository.findAll(pageable)
                .map(transactionMapper::toDto);
    }

    @Override
    @Transactional
    public TransactionResponseDTO createTransaction(CreateTransactionRequestDTO requestDTO) {

        Category category = categoryRepository.findById(requestDTO.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + requestDTO.categoryId()));

        Transaction created = transactionMapper.toEntity(requestDTO);
        created.setCategory(category);

        Transaction saved = transactionRepository.save(created);
        return transactionMapper.toDto(saved);
    }
}
