package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.dto.CategoryDTO;

import java.util.List;


public interface CategoryService {

    List<CategoryDTO> findAll();
}
