package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.dto.CategoryResponseDTO;
import com.bertan.budgetplanner.dto.CreateCategoryRequestDTO;

import java.util.List;


public interface CategoryService {

    List<CategoryResponseDTO> findAll();

    CategoryResponseDTO createCategory(CreateCategoryRequestDTO requestDTO);

    CategoryResponseDTO updateCategory(Long id, CreateCategoryRequestDTO requestDTO);
}
