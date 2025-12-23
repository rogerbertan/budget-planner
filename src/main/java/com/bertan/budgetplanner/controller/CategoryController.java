package com.bertan.budgetplanner.controller;

import com.bertan.budgetplanner.dto.CategoryResponseDTO;
import com.bertan.budgetplanner.dto.CreateCategoryRequestDTO;
import com.bertan.budgetplanner.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {

        return ResponseEntity.ok(categoryService.findAll());
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @RequestBody CreateCategoryRequestDTO requestDTO) {

        CategoryResponseDTO created = categoryService.createCategory(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @RequestBody CreateCategoryRequestDTO requestDTO) {

        CategoryResponseDTO updated = categoryService.updateCategory(id, requestDTO);
        return ResponseEntity.ok(updated);
    }
}
