package com.bertan.budgetplanner.service.impl;

import com.bertan.budgetplanner.domain.Category;
import com.bertan.budgetplanner.dto.CategoryDTO;
import com.bertan.budgetplanner.mapper.CategoryMapper;
import com.bertan.budgetplanner.repository.CategoryRepository;
import com.bertan.budgetplanner.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper = new CategoryMapper();

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toDtoList(categories);
    }
}
