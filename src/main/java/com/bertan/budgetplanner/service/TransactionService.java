package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.config.JWTUserData;
import com.bertan.budgetplanner.domain.category.Category;
import com.bertan.budgetplanner.domain.transaction.Transaction;
import com.bertan.budgetplanner.domain.category.Type;
import com.bertan.budgetplanner.domain.user.Role;
import com.bertan.budgetplanner.dto.CategoriesSummaryResponse;
import com.bertan.budgetplanner.dto.CreateTransactionRequest;
import com.bertan.budgetplanner.dto.TransactionResponse;
import com.bertan.budgetplanner.exception.ResourceNotFoundException;
import com.bertan.budgetplanner.mapper.TransactionMapper;
import com.bertan.budgetplanner.repository.CategoryRepository;
import com.bertan.budgetplanner.repository.TransactionRepository;
import com.bertan.budgetplanner.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository,
                              CategoryRepository categoryRepository,
                              UserRepository userRepository,
                              TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> findAll(Pageable pageable, JWTUserData principal) {

        Page<Transaction> transactions;
        if (principal.role() == Role.ADMIN) {
            transactions = transactionRepository.findAll(pageable);
        } else {
            transactions = transactionRepository.findAllByUserId(principal.userId(), pageable);
        }

        return transactions.map(transactionMapper::toDto);
    }

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest requestDTO, JWTUserData principal) {

        Category category = categoryRepository.findById(requestDTO.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", requestDTO.categoryId().toString()));

        Transaction created = transactionMapper.toEntity(requestDTO);
        created.setCategory(category);
        created.setUser(userRepository.getReferenceById(principal.userId()));

        Transaction saved = transactionRepository.save(created);
        return transactionMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id, JWTUserData principal) {
        Transaction transaction = findOwnedOrAnyIfAdmin(id, principal);

        return transactionMapper.toDto(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, CreateTransactionRequest requestDTO, JWTUserData principal) {

        Transaction existing = findOwnedOrAnyIfAdmin(id, principal);

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

    @Transactional
    public void deleteTransaction(Long id, JWTUserData principal) {

        Transaction existing = findOwnedOrAnyIfAdmin(id, principal);

        transactionRepository.delete(existing);
    }

    private Transaction findOwnedOrAnyIfAdmin(Long id, JWTUserData principal) {

        if (principal.role() == Role.ADMIN) {
            return transactionRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction", id.toString()));
        }

        return transactionRepository.findByIdAndUserId(id, principal.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id.toString()));
    }

    public BigDecimal getTotalIncome(JWTUserData principal) {

        if (principal.role() == Role.ADMIN) {
            return transactionRepository.sumAmountByType(Type.INCOME);
        }

        return transactionRepository.sumAmountByTypeAndUserId(Type.INCOME, principal.userId());
    }

    public BigDecimal getTotalExpense(JWTUserData principal) {

        if (principal.role() == Role.ADMIN) {
            return transactionRepository.sumAmountByType(Type.EXPENSE);
        }

        return transactionRepository.sumAmountByTypeAndUserId(Type.EXPENSE, principal.userId());
    }

    public BigDecimal getMonthlyIncome(int month, int year, JWTUserData principal) {

        if (principal.role() == Role.ADMIN) {
            return transactionRepository.sumAmountByMonthAndYearAndType(month, year, Type.INCOME);
        }

        return transactionRepository.sumAmountByMonthAndYearAndTypeAndUserId(month, year, Type.INCOME, principal.userId());
    }

    public BigDecimal getMonthlyExpense(int month, int year, JWTUserData principal) {

        if (principal.role() == Role.ADMIN) {
            return transactionRepository.sumAmountByMonthAndYearAndType(month, year, Type.EXPENSE);
        }

        return transactionRepository.sumAmountByMonthAndYearAndTypeAndUserId(month, year, Type.EXPENSE, principal.userId());
    }

    public List<CategoriesSummaryResponse> getCategoriesSummaries(int month, int year, JWTUserData principal) {

        if (principal.role() == Role.ADMIN) {
            return transactionRepository.findCategorySummariesByMonthAndYear(month, year);
        }

        return transactionRepository.findCategorySummariesByMonthAndYearAndUserId(month, year, principal.userId());
    }
}
