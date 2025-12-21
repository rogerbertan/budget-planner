package com.bertan.budgetplanner.mapper;

import com.bertan.budgetplanner.domain.Category;
import com.bertan.budgetplanner.dto.CategoryDTO;
import com.bertan.budgetplanner.dto.CreateCategoryDTO;

import java.util.List;

public class CategoryMapper {

    public CategoryDTO toDto(Category category) {
        CategoryDTO dto = new CategoryDTO(category.getId(), category.getName(), category.getType());
        return dto;
    }

    public Category toEntity(CreateCategoryDTO dto) {
        Category category = new Category();
        category.getName();
        category.getType();
        return category;
    }

    public List<CategoryDTO> toDtoList(List<Category> categories) {
        return categories.stream().map(this::toDto).toList();
    }

    public List<CreateCategoryDTO> toCreateDtoList(List<Category> categories) {
        return categories.stream().map(category -> new CreateCategoryDTO(category.getName(), category.getType())).toList();
    }

}
