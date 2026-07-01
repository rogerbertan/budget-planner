package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.domain.category.Category;
import com.bertan.budgetplanner.domain.transaction.Transaction;
import com.bertan.budgetplanner.domain.category.Type;
import com.bertan.budgetplanner.dto.CategoriesSummaryResponse;
import com.bertan.budgetplanner.dto.CreateTransactionRequest;
import com.bertan.budgetplanner.dto.TransactionResponse;
import com.bertan.budgetplanner.exception.ResourceNotFoundException;
import com.bertan.budgetplanner.mapper.TransactionMapper;
import com.bertan.budgetplanner.repository.CategoryRepository;
import com.bertan.budgetplanner.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldReturnAllTransactionsPaged() {
        Pageable pageable = PageRequest.of(0, 10);
        Transaction transaction = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());
        TransactionResponse dto = new TransactionResponse(1L, Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now(), null);

        when(transactionRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(transaction)));
        when(transactionMapper.toDto(transaction)).thenReturn(dto);

        Page<TransactionResponse> result = transactionService.findAll(pageable);

        assertThat(result.getContent()).containsExactly(dto);
    }

    @Test
    void shouldReturnEmptyPageWhenNoTransactionsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        when(transactionRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        Page<TransactionResponse> result = transactionService.findAll(pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void shouldCreateTransactionWhenCategoryExists() {
        Long categoryId = 1L;
        CreateTransactionRequest request = new CreateTransactionRequest(
                Type.EXPENSE, BigDecimal.TEN, "Lunch", categoryId, LocalDate.now());
        Category category = new Category("Food", Type.EXPENSE);
        Transaction mapped = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());
        Transaction saved = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", category, LocalDate.now());
        TransactionResponse responseDTO = new TransactionResponse(
                1L, Type.EXPENSE, BigDecimal.TEN, "Lunch", categoryId, LocalDate.now(), null);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionMapper.toEntity(request)).thenReturn(mapped);
        when(transactionRepository.save(mapped)).thenReturn(saved);
        when(transactionMapper.toDto(saved)).thenReturn(responseDTO);

        TransactionResponse result = transactionService.createTransaction(request);

        assertThat(result).isEqualTo(responseDTO);
        assertThat(mapped.getCategory()).isEqualTo(category);
    }

    @Test
    void shouldThrowExceptionWhenCreatingTransactionWithNonExistentCategory() {
        Long categoryId = 99L;
        CreateTransactionRequest request = new CreateTransactionRequest(
                Type.EXPENSE, BigDecimal.TEN, "Lunch", categoryId, LocalDate.now());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found: " + categoryId);

        verify(transactionRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldReturnTransactionByIdWhenItExists() {
        Long id = 1L;
        Transaction transaction = new Transaction(Type.INCOME, BigDecimal.valueOf(100), "Salary", null, LocalDate.now());
        TransactionResponse dto = new TransactionResponse(id, Type.INCOME, BigDecimal.valueOf(100), "Salary", null, LocalDate.now(), null);

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toDto(transaction)).thenReturn(dto);

        TransactionResponse result = transactionService.getTransactionById(id);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFoundById() {
        Long id = 99L;
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransactionById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found: " + id);
    }

    @Test
    void shouldUpdateTransactionWhenItAndCategoryExist() {
        Long id = 1L;
        Long categoryId = 2L;
        CreateTransactionRequest request = new CreateTransactionRequest(
                Type.INCOME, BigDecimal.valueOf(200), "Bonus", categoryId, LocalDate.now());
        Transaction existing = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());
        Category category = new Category("Bonus", Type.INCOME);
        TransactionResponse responseDTO = new TransactionResponse(
                id, Type.INCOME, BigDecimal.valueOf(200), "Bonus", categoryId, LocalDate.now(), null);

        when(transactionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(existing)).thenReturn(existing);
        when(transactionMapper.toDto(existing)).thenReturn(responseDTO);

        TransactionResponse result = transactionService.updateTransaction(id, request);

        assertThat(result).isEqualTo(responseDTO);
        assertThat(existing.getType()).isEqualTo(Type.INCOME);
        assertThat(existing.getAmount()).isEqualTo(BigDecimal.valueOf(200));
        assertThat(existing.getDescription()).isEqualTo("Bonus");
        assertThat(existing.getCategory()).isEqualTo(category);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentTransaction() {
        Long id = 99L;
        CreateTransactionRequest request = new CreateTransactionRequest(
                Type.INCOME, BigDecimal.valueOf(200), "Bonus", 1L, LocalDate.now());

        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.updateTransaction(id, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found: " + id);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingTransactionWithNonExistentCategory() {
        Long id = 1L;
        Long categoryId = 99L;
        CreateTransactionRequest request = new CreateTransactionRequest(
                Type.INCOME, BigDecimal.valueOf(200), "Bonus", categoryId, LocalDate.now());
        Transaction existing = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());

        when(transactionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.updateTransaction(id, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found: " + categoryId);
    }

    @Test
    void shouldDeleteTransactionWhenItExists() {
        Long id = 1L;
        Transaction existing = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());

        when(transactionRepository.findById(id)).thenReturn(Optional.of(existing));

        transactionService.deleteTransaction(id);

        verify(transactionRepository).delete(existing);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTransaction() {
        Long id = 99L;
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.deleteTransaction(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found: " + id);

        verify(transactionRepository, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldReturnTotalIncome() {
        when(transactionRepository.sumAmountByType(Type.INCOME)).thenReturn(BigDecimal.valueOf(500));

        BigDecimal result = transactionService.getTotalIncome();

        assertThat(result).isEqualTo(BigDecimal.valueOf(500));
    }

    @Test
    void shouldReturnZeroTotalExpenseWhenNoExpensesExist() {
        when(transactionRepository.sumAmountByType(Type.EXPENSE)).thenReturn(BigDecimal.ZERO);

        BigDecimal result = transactionService.getTotalExpense();

        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void shouldReturnMonthlyIncome() {
        when(transactionRepository.sumAmountByMonthAndYearAndType(6, 2026, Type.INCOME))
                .thenReturn(BigDecimal.valueOf(1000));

        BigDecimal result = transactionService.getMonthlyIncome(6, 2026);

        assertThat(result).isEqualTo(BigDecimal.valueOf(1000));
    }

    @Test
    void shouldReturnMonthlyExpense() {
        when(transactionRepository.sumAmountByMonthAndYearAndType(6, 2026, Type.EXPENSE))
                .thenReturn(BigDecimal.valueOf(300));

        BigDecimal result = transactionService.getMonthlyExpense(6, 2026);

        assertThat(result).isEqualTo(BigDecimal.valueOf(300));
    }

    @Test
    void shouldReturnCategoriesSummaries() {
        CategoriesSummaryResponse summary = new CategoriesSummaryResponse("Food", BigDecimal.ZERO, BigDecimal.TEN);
        when(transactionRepository.findCategorySummariesByMonthAndYear(6, 2026)).thenReturn(List.of(summary));

        List<CategoriesSummaryResponse> result = transactionService.getCategoriesSummaries(6, 2026);

        assertThat(result).containsExactly(summary);
    }

    @Test
    void shouldReturnEmptyCategoriesSummariesWhenNoneExist() {
        when(transactionRepository.findCategorySummariesByMonthAndYear(6, 2026)).thenReturn(List.of());

        List<CategoriesSummaryResponse> result = transactionService.getCategoriesSummaries(6, 2026);

        assertThat(result).isEmpty();
    }
}