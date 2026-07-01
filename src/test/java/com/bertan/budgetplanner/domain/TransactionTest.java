package com.bertan.budgetplanner.domain;

import com.bertan.budgetplanner.domain.category.Category;
import com.bertan.budgetplanner.domain.category.Type;
import com.bertan.budgetplanner.domain.transaction.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionTest {

    @Test
    void shouldCreateTransactionWithConstructorArgs() {
        Category category = new Category("Food", Type.EXPENSE);
        LocalDate date = LocalDate.now();
        Transaction transaction = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", category, date);

        assertThat(transaction.getType()).isEqualTo(Type.EXPENSE);
        assertThat(transaction.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(transaction.getDescription()).isEqualTo("Lunch");
        assertThat(transaction.getCategory()).isEqualTo(category);
        assertThat(transaction.getTransactionDate()).isEqualTo(date);
        assertThat(transaction.getId()).isNull();
        assertThat(transaction.getCreatedAt()).isNull();
    }

    @Test
    void shouldAllowNullCategory() {
        Transaction transaction = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());

        assertThat(transaction.getCategory()).isNull();
    }

    @Test
    void shouldSetCreatedAtWhenOnCreateIsInvoked() {
        Transaction transaction = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());

        ReflectionTestUtils.invokeMethod(transaction, "onCreate");

        assertThat(transaction.getCreatedAt()).isNotNull();
        assertThat(transaction.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        Transaction transaction1 = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());
        Transaction transaction2 = new Transaction(Type.INCOME, BigDecimal.ONE, "Salary", null, LocalDate.now());
        ReflectionTestUtils.setField(transaction1, "id", 1L);
        ReflectionTestUtils.setField(transaction2, "id", 1L);

        assertThat(transaction1).isEqualTo(transaction2);
        assertThat(transaction1.hashCode()).isEqualTo(transaction2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        Transaction transaction1 = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());
        Transaction transaction2 = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());
        ReflectionTestUtils.setField(transaction1, "id", 1L);
        ReflectionTestUtils.setField(transaction2, "id", 2L);

        assertThat(transaction1).isNotEqualTo(transaction2);
    }

    @Test
    void shouldNotBeEqualToNull() {
        Transaction transaction = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());

        assertThat(transaction).isNotEqualTo(null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        Transaction transaction = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());

        assertThat(transaction).isNotEqualTo("not a transaction");
    }

    @Test
    void shouldHaveConsistentHashCodeWhenIdIsNull() {
        Transaction transaction1 = new Transaction(Type.EXPENSE, BigDecimal.TEN, "Lunch", null, LocalDate.now());
        Transaction transaction2 = new Transaction(Type.INCOME, BigDecimal.ONE, "Salary", null, LocalDate.now());

        assertThat(transaction1.hashCode()).isEqualTo(transaction2.hashCode());
    }
}
