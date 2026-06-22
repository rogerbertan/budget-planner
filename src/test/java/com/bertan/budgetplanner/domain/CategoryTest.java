package com.bertan.budgetplanner.domain;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Test
    void shouldCreateCategoryWithConstructorArgs() {
        Category category = new Category("Salary", Type.INCOME);

        assertThat(category.getName()).isEqualTo("Salary");
        assertThat(category.getType()).isEqualTo(Type.INCOME);
        assertThat(category.getId()).isNull();
        assertThat(category.getCreatedAt()).isNull();
    }

    @Test
    void shouldSetCreatedAtWhenOnCreateIsInvoked() {
        Category category = new Category("Salary", Type.INCOME);

        ReflectionTestUtils.invokeMethod(category, "onCreate");

        assertThat(category.getCreatedAt()).isNotNull();
        assertThat(category.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        Category category1 = new Category("Salary", Type.INCOME);
        Category category2 = new Category("Groceries", Type.EXPENSE);
        ReflectionTestUtils.setField(category1, "id", 1L);
        ReflectionTestUtils.setField(category2, "id", 1L);

        assertThat(category1).isEqualTo(category2);
        assertThat(category1.hashCode()).isEqualTo(category2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        Category category1 = new Category("Salary", Type.INCOME);
        Category category2 = new Category("Salary", Type.INCOME);
        ReflectionTestUtils.setField(category1, "id", 1L);
        ReflectionTestUtils.setField(category2, "id", 2L);

        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    void shouldNotBeEqualToNull() {
        Category category = new Category("Salary", Type.INCOME);

        assertThat(category).isNotEqualTo(null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {
        Category category = new Category("Salary", Type.INCOME);

        assertThat(category).isNotEqualTo("not a category");
    }

    @Test
    void shouldHaveConsistentHashCodeWhenIdIsNull() {
        Category category1 = new Category("Salary", Type.INCOME);
        Category category2 = new Category("Groceries", Type.EXPENSE);

        assertThat(category1.hashCode()).isEqualTo(category2.hashCode());
    }
}
