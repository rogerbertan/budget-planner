package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.domain.category.Category;
import com.bertan.budgetplanner.domain.transaction.Transaction;
import com.bertan.budgetplanner.domain.category.Type;
import com.bertan.budgetplanner.dto.CategoriesSummaryResponseDTO;
import com.bertan.budgetplanner.dto.CreateTransactionRequestDTO;
import com.bertan.budgetplanner.dto.TransactionResponseDTO;
import com.bertan.budgetplanner.exception.ResourceNotFoundException;
import com.bertan.budgetplanner.mapper.TransactionMapper;
import com.bertan.budgetplanner.repository.CategoryRepository;
import com.bertan.budgetplanner.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository,
                              CategoryRepository categoryRepository,
                              TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponseDTO> findAll(Pageable pageable) {

        return transactionRepository.findAll(pageable)
                .map(transactionMapper::toDto);
    }

    @Transactional
    public TransactionResponseDTO createTransaction(CreateTransactionRequestDTO requestDTO) {

        Category category = categoryRepository.findById(requestDTO.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", requestDTO.categoryId().toString()));

        Transaction created = transactionMapper.toEntity(requestDTO);
        created.setCategory(category);

        Transaction saved = transactionRepository.save(created);
        return transactionMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public TransactionResponseDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id.toString()));

        return transactionMapper.toDto(transaction);
    }

    public TransactionResponseDTO updateTransaction(Long id, CreateTransactionRequestDTO requestDTO) {

        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id.toString()));

        Category category = categoryRepository.findById(requestDTO.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", requestDTO.categoryId().toString()));

        existing.setType(requestDTO.type());
        existing.setAmount(requestDTO.amount());
        existing.setDescription(requestDTO.description());
        existing.setTransactionDate(requestDTO.transactionDate());
        existing.setCategory(category);

        Transaction updated = transactionRepository.save(existing);
        return transactionMapper.toDto(updated);
    }

    public void deleteTransaction(Long id) {

        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id.toString()));

        transactionRepository.delete(existing);
    }

    public BigDecimal getTotalIncome() {

        return transactionRepository.sumAmountByType(Type.INCOME);
    }

    public BigDecimal getTotalExpense() {

        return transactionRepository.sumAmountByType(Type.EXPENSE);
    }

    public BigDecimal getMonthlyIncome(int month, int year) {

        return transactionRepository.sumAmountByMonthAndYearAndType(month, year, Type.INCOME);
    }

    public BigDecimal getMonthlyExpense(int month, int year) {

        return transactionRepository.sumAmountByMonthAndYearAndType(month, year, Type.EXPENSE);
    }

    public List<CategoriesSummaryResponseDTO> getCategoriesSummaries(int month, int year) {

        return transactionRepository.findCategorySummariesByMonthAndYear(month, year);
    }
}
