package com.bertan.budgetplanner.service.impl;

import com.bertan.budgetplanner.domain.Category;
import com.bertan.budgetplanner.domain.Transaction;
import com.bertan.budgetplanner.domain.Type;
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

import java.math.BigDecimal;

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

    @Override
    @Transactional(readOnly = true)
    public TransactionResponseDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));

        return transactionMapper.toDto(transaction);
    }

    @Override
    public TransactionResponseDTO updateTransaction(Long id, CreateTransactionRequestDTO requestDTO) {

        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));

        Category category = categoryRepository.findById(requestDTO.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + requestDTO.categoryId()));

        existing.setType(requestDTO.type());
        existing.setAmount(requestDTO.amount());
        existing.setDescription(requestDTO.description());
        existing.setTransactionDate(requestDTO.transactionDate());
        existing.setCategory(category);

        Transaction updated = transactionRepository.save(existing);
        return transactionMapper.toDto(updated);
    }

    @Override
    public void deleteTransaction(Long id) {

        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));

        transactionRepository.delete(existing);
    }

    @Override
    public BigDecimal getTotalIncome() {

        return transactionRepository.sumAmountByType(Type.INCOME);
    }

    @Override
    public BigDecimal getTotalExpense() {

        return transactionRepository.sumAmountByType(Type.EXPENSE);
    }

    @Override
    public BigDecimal getMonthlyIncome(int month, int year) {

        return transactionRepository.sumAmountByMonthAndYearAndType(month, year, Type.INCOME);
    }

    @Override
    public BigDecimal getMonthlyExpense(int month, int year) {

        return transactionRepository.sumAmountByMonthAndYearAndType(month, year, Type.EXPENSE);
    }
}
