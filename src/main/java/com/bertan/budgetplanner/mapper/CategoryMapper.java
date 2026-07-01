package com.bertan.budgetplanner.mapper;

import com.bertan.budgetplanner.domain.category.Category;
import com.bertan.budgetplanner.dto.CategoryResponse;
import com.bertan.budgetplanner.dto.CreateCategoryRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

    public CategoryResponse toDto(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getType());
    }

    public Category toEntity(CreateCategoryRequest dto) {
        Category category = new Category();
        category.setName(dto.name());
        category.setType(dto.type());
        return category;
    }

    public List<CategoryResponse> toDtoList(List<Category> categories) {
        return categories.stream()
                .map(this::toDto)
                .toList();
    }

}
