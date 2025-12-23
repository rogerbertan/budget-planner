package com.bertan.budgetplanner.mapper;

import com.bertan.budgetplanner.domain.Category;
import com.bertan.budgetplanner.dto.CategoryResponseDTO;
import com.bertan.budgetplanner.dto.CreateCategoryRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

    public CategoryResponseDTO toDto(Category category) {
        return new CategoryResponseDTO(category.getId(), category.getName(), category.getType());
    }

    public Category toEntity(CreateCategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.name());
        category.setType(dto.type());
        return category;
    }

    public List<CategoryResponseDTO> toDtoList(List<Category> categories) {
        return categories.stream()
                .map(this::toDto)
                .toList();
    }

}
