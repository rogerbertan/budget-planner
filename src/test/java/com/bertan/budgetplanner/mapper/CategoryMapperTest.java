package com.bertan.budgetplanner.mapper;

import com.bertan.budgetplanner.domain.category.Category;
import com.bertan.budgetplanner.domain.category.Type;
import com.bertan.budgetplanner.dto.CategoryResponse;
import com.bertan.budgetplanner.dto.CreateCategoryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperTest {

    private final CategoryMapper categoryMapper = new CategoryMapper();

    @Test
    void shouldMapCategoryToDto() {
        Category category = new Category("Salary", Type.INCOME);
        ReflectionTestUtils.setField(category, "id", 1L);

        CategoryResponse dto = categoryMapper.toDto(category);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Salary");
        assertThat(dto.type()).isEqualTo(Type.INCOME);
    }

    @Test
    void shouldMapCreateCategoryRequestDtoToEntity() {
        CreateCategoryRequest request = new CreateCategoryRequest("Groceries", Type.EXPENSE);

        Category entity = categoryMapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("Groceries");
        assertThat(entity.getType()).isEqualTo(Type.EXPENSE);
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
    }

    @Test
    void shouldMapListOfCategoriesToDtoList() {
        Category category1 = new Category("Salary", Type.INCOME);
        Category category2 = new Category("Groceries", Type.EXPENSE);

        List<CategoryResponse> result = categoryMapper.toDtoList(List.of(category1, category2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Salary");
        assertThat(result.get(1).name()).isEqualTo("Groceries");
    }

    @Test
    void shouldReturnEmptyListWhenMappingEmptyCategoryList() {
        List<CategoryResponse> result = categoryMapper.toDtoList(List.of());

        assertThat(result).isEmpty();
    }
}
