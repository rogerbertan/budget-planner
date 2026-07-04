package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.config.JWTUserData;
import com.bertan.budgetplanner.domain.category.Category;
import com.bertan.budgetplanner.domain.transaction.Transaction;
import com.bertan.budgetplanner.domain.category.Type;
import com.bertan.budgetplanner.domain.user.Role;
import com.bertan.budgetplanner.domain.user.User;
import com.bertan.budgetplanner.dto.CategoriesSummaryResponse;
import com.bertan.budgetplanner.dto.CreateTransactionRequest;
import com.bertan.budgetplanner.dto.TransactionResponse;
import com.bertan.budgetplanner.exception.ResourceNotFoundException;
import com.bertan.budgetplanner.mapper.TransactionMapper;
import com.bertan.budgetplanner.repository.CategoryRepository;
import com.bertan.budgetplanner.repository.TransactionRepository;
import com.bertan.budgetplanner.repository.UserRepository;
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

    private static final JWTUserData ADMIN = new JWTUserData(1L, "admin@test.com", Role.ADMIN);
    private static final JWTUserData USER = new JWTUserData(2L, "user@test.com", Role.USER);

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldReturnAllTransactionsPagedForAdmin() {
        Pageable pageable = PageRequest.of(0, 10);
        Transaction transaction = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, null, LocalDate.now());
        TransactionResponse dto = new TransactionResponse(1L, Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now(), null);

        when(transactionRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(transaction)));
        when(transactionMapper.toDto(transaction)).thenReturn(dto);

        Page<TransactionResponse> result = transactionService.findAll(pageable, ADMIN);

        assertThat(result.getContent()).containsExactly(dto);
    }

    @Test
    void shouldReturnOnlyOwnTransactionsPagedForUser() {
        Pageable pageable = PageRequest.of(0, 10);
        Transaction transaction = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, null, LocalDate.now());
        TransactionResponse dto = new TransactionResponse(1L, Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now(), null);

        when(transactionRepository.findAllByUserId(USER.userId(), pageable)).thenReturn(new PageImpl<>(List.of(transaction)));
        when(transactionMapper.toDto(transaction)).thenReturn(dto);

        Page<TransactionResponse> result = transactionService.findAll(pageable, USER);

        assertThat(result.getContent()).containsExactly(dto);
        verify(transactionRepository, never()).findAll(pageable);
    }

    @Test
    void shouldReturnEmptyPageWhenNoTransactionsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        when(transactionRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        Page<TransactionResponse> result = transactionService.findAll(pageable, ADMIN);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void shouldCreateTransactionWhenCategoryExists() {
        Long categoryId = 1L;
        CreateTransactionRequest request = new CreateTransactionRequest(
                Type.EXPENSE, BigDecimal.TEN, "Lunch", categoryId, LocalDate.now());
        Category category = new Category("Food", Type.EXPENSE);
        User owner = new User("Test User", "user@test.com", "password");
        Transaction mapped = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, null, LocalDate.now());
        Transaction saved = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", category, owner, LocalDate.now());
        TransactionResponse responseDTO = new TransactionResponse(
                1L, Type.EXPENSE, BigDecimal.TEN, "Lunch", categoryId, LocalDate.now(), null);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionMapper.toEntity(request)).thenReturn(mapped);
        when(userRepository.getReferenceById(USER.userId())).thenReturn(owner);
        when(transactionRepository.save(mapped)).thenReturn(saved);
        when(transactionMapper.toDto(saved)).thenReturn(responseDTO);

        TransactionResponse result = transactionService.createTransaction(request, USER);

        assertThat(result).isEqualTo(responseDTO);
        assertThat(mapped.getCategory()).isEqualTo(category);
        assertThat(mapped.getUser()).isEqualTo(owner);
    }

    @Test
    void shouldThrowExceptionWhenCreatingTransactionWithNonExistentCategory() {
        Long categoryId = 99L;
        CreateTransactionRequest request = new CreateTransactionRequest(
                Type.EXPENSE, BigDecimal.TEN, "Lunch", categoryId, LocalDate.now());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(request, USER))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found: " + categoryId);

        verify(transactionRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldReturnTransactionByIdWhenItExists() {
        Long id = 1L;
        Transaction transaction = new Transaction(Type.INCOME, BigDecimal.valueOf(100), "Salary", null, null, LocalDate.now());
        TransactionResponse dto = new TransactionResponse(id, Type.INCOME, BigDecimal.valueOf(100), "Salary", null, LocalDate.now(), null);

        when(transactionRepository.findByIdAndUserId(id, USER.userId())).thenReturn(Optional.of(transaction));
        when(transactionMapper.toDto(transaction)).thenReturn(dto);

        TransactionResponse result = transactionService.getTransactionById(id, USER);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void shouldReturnAnyTransactionByIdWhenAdmin() {
        Long id = 1L;
        Transaction transaction = new Transaction(Type.INCOME, BigDecimal.valueOf(100), "Salary", null, null, LocalDate.now());
        TransactionResponse dto = new TransactionResponse(id, Type.INCOME, BigDecimal.valueOf(100), "Salary", null, LocalDate.now(), null);

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toDto(transaction)).thenReturn(dto);

        TransactionResponse result = transactionService.getTransactionById(id, ADMIN);

        assertThat(result).isEqualTo(dto);
        verify(transactionRepository, never()).findByIdAndUserId(id, ADMIN.userId());
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFoundById() {
        Long id = 99L;
        when(transactionRepository.findByIdAndUserId(id, USER.userId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransactionById(id, USER))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found: " + id);
    }

    @Test
    void shouldThrowExceptionWhenUserTriesToAccessAnotherUsersTransaction() {
        Long id = 1L;
        when(transactionRepository.findByIdAndUserId(id, USER.userId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransactionById(id, USER))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found: " + id);
    }

    @Test
    void shouldUpdateTransactionWhenItAndCategoryExist() {
        Long id = 1L;
        Long categoryId = 2L;
        CreateTransactionRequest request = new CreateTransactionRequest(
                Type.INCOME, BigDecimal.valueOf(200), "Bonus", categoryId, LocalDate.now());
        Transaction existing = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, null, LocalDate.now());
        Category category = new Category("Bonus", Type.INCOME);
        TransactionResponse responseDTO = new TransactionResponse(
                id, Type.INCOME, BigDecimal.valueOf(200), "Bonus", categoryId, LocalDate.now(), null);

        when(transactionRepository.findByIdAndUserId(id, USER.userId())).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(existing)).thenReturn(existing);
        when(transactionMapper.toDto(existing)).thenReturn(responseDTO);

        TransactionResponse result = transactionService.updateTransaction(id, request, USER);

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

        when(transactionRepository.findByIdAndUserId(id, USER.userId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.updateTransaction(id, request, USER))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found: " + id);
    }

    @Test
    void shouldThrowExceptionWhenUserTriesToUpdateAnotherUsersTransaction() {
        Long id = 1L;
        CreateTransactionRequest request = new CreateTransactionRequest(
                Type.INCOME, BigDecimal.valueOf(200), "Bonus", 1L, LocalDate.now());

        when(transactionRepository.findByIdAndUserId(id, USER.userId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.updateTransaction(id, request, USER))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found: " + id);

        verify(categoryRepository, never()).findById(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingTransactionWithNonExistentCategory() {
        Long id = 1L;
        Long categoryId = 99L;
        CreateTransactionRequest request = new CreateTransactionRequest(
                Type.INCOME, BigDecimal.valueOf(200), "Bonus", categoryId, LocalDate.now());
        Transaction existing = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, null, LocalDate.now());

        when(transactionRepository.findByIdAndUserId(id, USER.userId())).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.updateTransaction(id, request, USER))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found: " + categoryId);
    }

    @Test
    void shouldDeleteTransactionWhenItExists() {
        Long id = 1L;
        Transaction existing = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, null, LocalDate.now());

        when(transactionRepository.findByIdAndUserId(id, USER.userId())).thenReturn(Optional.of(existing));

        transactionService.deleteTransaction(id, USER);

        verify(transactionRepository).delete(existing);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTransaction() {
        Long id = 99L;
        when(transactionRepository.findByIdAndUserId(id, USER.userId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.deleteTransaction(id, USER))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found: " + id);

        verify(transactionRepository, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldThrowExceptionWhenUserTriesToDeleteAnotherUsersTransaction() {
        Long id = 1L;
        when(transactionRepository.findByIdAndUserId(id, USER.userId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.deleteTransaction(id, USER))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Transaction not found: " + id);

        verify(transactionRepository, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldReturnTotalIncomeForAdmin() {
        when(transactionRepository.sumAmountByType(Type.INCOME)).thenReturn(BigDecimal.valueOf(500));

        BigDecimal result = transactionService.getTotalIncome(ADMIN);

        assertThat(result).isEqualTo(BigDecimal.valueOf(500));
    }

    @Test
    void shouldReturnOwnTotalIncomeForUser() {
        when(transactionRepository.sumAmountByTypeAndUserId(Type.INCOME, USER.userId())).thenReturn(BigDecimal.valueOf(500));

        BigDecimal result = transactionService.getTotalIncome(USER);

        assertThat(result).isEqualTo(BigDecimal.valueOf(500));
        verify(transactionRepository, never()).sumAmountByType(Type.INCOME);
    }

    @Test
    void shouldReturnZeroTotalExpenseWhenNoExpensesExist() {
        when(transactionRepository.sumAmountByType(Type.EXPENSE)).thenReturn(BigDecimal.ZERO);

        BigDecimal result = transactionService.getTotalExpense(ADMIN);

        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void shouldReturnMonthlyIncome() {
        when(transactionRepository.sumAmountByMonthAndYearAndType(6, 2026, Type.INCOME))
                .thenReturn(BigDecimal.valueOf(1000));

        BigDecimal result = transactionService.getMonthlyIncome(6, 2026, ADMIN);

        assertThat(result).isEqualTo(BigDecimal.valueOf(1000));
    }

    @Test
    void shouldReturnOwnMonthlyIncomeForUser() {
        when(transactionRepository.sumAmountByMonthAndYearAndTypeAndUserId(6, 2026, Type.INCOME, USER.userId()))
                .thenReturn(BigDecimal.valueOf(1000));

        BigDecimal result = transactionService.getMonthlyIncome(6, 2026, USER);

        assertThat(result).isEqualTo(BigDecimal.valueOf(1000));
    }

    @Test
    void shouldReturnMonthlyExpense() {
        when(transactionRepository.sumAmountByMonthAndYearAndType(6, 2026, Type.EXPENSE))
                .thenReturn(BigDecimal.valueOf(300));

        BigDecimal result = transactionService.getMonthlyExpense(6, 2026, ADMIN);

        assertThat(result).isEqualTo(BigDecimal.valueOf(300));
    }

    @Test
    void shouldReturnCategoriesSummaries() {
        CategoriesSummaryResponse summary = new CategoriesSummaryResponse("Food", BigDecimal.ZERO, BigDecimal.TEN);
        when(transactionRepository.findCategorySummariesByMonthAndYear(6, 2026)).thenReturn(List.of(summary));

        List<CategoriesSummaryResponse> result = transactionService.getCategoriesSummaries(6, 2026, ADMIN);

        assertThat(result).containsExactly(summary);
    }

    @Test
    void shouldReturnOwnCategoriesSummariesForUser() {
        CategoriesSummaryResponse summary = new CategoriesSummaryResponse("Food", BigDecimal.ZERO, BigDecimal.TEN);
        when(transactionRepository.findCategorySummariesByMonthAndYearAndUserId(6, 2026, USER.userId()))
                .thenReturn(List.of(summary));

        List<CategoriesSummaryResponse> result = transactionService.getCategoriesSummaries(6, 2026, USER);

        assertThat(result).containsExactly(summary);
    }

    @Test
    void shouldReturnEmptyCategoriesSummariesWhenNoneExist() {
        when(transactionRepository.findCategorySummariesByMonthAndYear(6, 2026)).thenReturn(List.of());

        List<CategoriesSummaryResponse> result = transactionService.getCategoriesSummaries(6, 2026, ADMIN);

        assertThat(result).isEmpty();
    }
}