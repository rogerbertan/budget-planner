package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.domain.Category;
import com.bertan.budgetplanner.dto.CategoryResponseDTO;
import com.bertan.budgetplanner.dto.CreateCategoryRequestDTO;
import com.bertan.budgetplanner.exception.ResourceNotFoundException;
import com.bertan.budgetplanner.mapper.CategoryMapper;
import com.bertan.budgetplanner.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findAll() {

        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toDtoList(categories);
    }

    @Transactional
    public CategoryResponseDTO createCategory(CreateCategoryRequestDTO requestDTO) {

        Category category = categoryMapper.toEntity(requestDTO);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CreateCategoryRequestDTO requestDTO) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id.toString()));

        category.setName(requestDTO.name());
        category.setType(requestDTO.type());
        return categoryMapper.toDto(category);
    }

    @Transactional
    public void deleteCategory(Long id) {

        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", id.toString());
        }

        categoryRepository.deleteById(id);
    }
}
