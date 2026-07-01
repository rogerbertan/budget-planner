package com.bertan.budgetplanner.mapper;

import com.bertan.budgetplanner.domain.category.Category;
import com.bertan.budgetplanner.domain.transaction.Transaction;
import com.bertan.budgetplanner.domain.category.Type;
import com.bertan.budgetplanner.dto.CreateTransactionRequest;
import com.bertan.budgetplanner.dto.TransactionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {

    private final TransactionMapper transactionMapper = new TransactionMapper();

    @Test
    void shouldMapTransactionToDtoWithCategory() {
        Category category = new Category("Food", Type.EXPENSE);
        ReflectionTestUtils.setField(category, "id", 5L);
        Transaction transaction = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", category, LocalDate.now());
        ReflectionTestUtils.setField(transaction, "id", 1L);

        TransactionResponse dto = transactionMapper.toDto(transaction);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.type()).isEqualTo(Type.EXPENSE);
        assertThat(dto.amount()).isEqualTo(BigDecimal.TEN);
        assertThat(dto.description()).isEqualTo("Lunch");
        assertThat(dto.category()).isEqualTo(5L);
        assertThat(dto.transactionDate()).isEqualTo(transaction.getTransactionDate());
    }

    @Test
    void shouldMapTransactionToDtoWhenCategoryIsNull() {
        Transaction transaction = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());

        TransactionResponse dto = transactionMapper.toDto(transaction);

        assertThat(dto.category()).isNull();
    }

    @Test
    void shouldMapCreateTransactionRequestDtoToEntity() {
        LocalDate date = LocalDate.now();
        CreateTransactionRequest request = new CreateTransactionRequest(
                Type.INCOME, BigDecimal.valueOf(100), "Salary", 3L, date);

        Transaction entity = transactionMapper.toEntity(request);

        assertThat(entity.getType()).isEqualTo(Type.INCOME);
        assertThat(entity.getAmount()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(entity.getDescription()).isEqualTo("Salary");
        assertThat(entity.getTransactionDate()).isEqualTo(date);
        assertThat(entity.getCategory()).isNull();
    }

    @Test
    void shouldMapListOfTransactionsToDtoList() {
        Transaction transaction1 = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());
        Transaction transaction2 = new Transaction(Type.INCOME, BigDecimal.ONE, "Salary", null, LocalDate.now());

        List<TransactionResponse> result = transactionMapper.toDtoList(List.of(transaction1, transaction2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).description()).isEqualTo("Lunch");
        assertThat(result.get(1).description()).isEqualTo("Salary");
    }

    @Test
    void shouldReturnEmptyListWhenMappingEmptyTransactionList() {
        List<TransactionResponse> result = transactionMapper.toDtoList(List.of());

        assertThat(result).isEmpty();
    }
}
