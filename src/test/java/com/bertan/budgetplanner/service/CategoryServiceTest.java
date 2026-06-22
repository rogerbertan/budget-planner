package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.domain.Category;
import com.bertan.budgetplanner.domain.Type;
import com.bertan.budgetplanner.dto.CategoryResponseDTO;
import com.bertan.budgetplanner.dto.CreateCategoryRequestDTO;
import com.bertan.budgetplanner.exception.ResourceNotFoundException;
import com.bertan.budgetplanner.mapper.CategoryMapper;
import com.bertan.budgetplanner.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldReturnAllCategories() {
        Category category = new Category("Salary", Type.INCOME);
        CategoryResponseDTO dto = new CategoryResponseDTO(1L, "Salary", Type.INCOME);

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toDtoList(List.of(category))).thenReturn(List.of(dto));

        List<CategoryResponseDTO> result = categoryService.findAll();

        assertThat(result).containsExactly(dto);
    }

    @Test
    void shouldReturnEmptyListWhenNoCategoriesExist() {
        when(categoryRepository.findAll()).thenReturn(List.of());
        when(categoryMapper.toDtoList(List.of())).thenReturn(List.of());

        List<CategoryResponseDTO> result = categoryService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCreateCategory() {
        CreateCategoryRequestDTO request = new CreateCategoryRequestDTO("Groceries", Type.EXPENSE);
        Category entity = new Category("Groceries", Type.EXPENSE);
        Category saved = new Category("Groceries", Type.EXPENSE);
        CategoryResponseDTO responseDTO = new CategoryResponseDTO(1L, "Groceries", Type.EXPENSE);

        when(categoryMapper.toEntity(request)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(saved);
        when(categoryMapper.toDto(saved)).thenReturn(responseDTO);

        CategoryResponseDTO result = categoryService.createCategory(request);

        assertThat(result).isEqualTo(responseDTO);
        verify(categoryRepository).save(entity);
    }

    @Test
    void shouldUpdateCategoryWhenItExists() {
        Long id = 1L;
        CreateCategoryRequestDTO request = new CreateCategoryRequestDTO("Updated Name", Type.INCOME);
        Category existing = new Category("Old Name", Type.EXPENSE);
        CategoryResponseDTO responseDTO = new CategoryResponseDTO(id, "Updated Name", Type.INCOME);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryMapper.toDto(existing)).thenReturn(responseDTO);

        CategoryResponseDTO result = categoryService.updateCategory(id, request);

        assertThat(result).isEqualTo(responseDTO);
        assertThat(existing.getName()).isEqualTo("Updated Name");
        assertThat(existing.getType()).isEqualTo(Type.INCOME);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentCategory() {
        Long id = 99L;
        CreateCategoryRequestDTO request = new CreateCategoryRequestDTO("Updated Name", Type.INCOME);

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(id, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found: " + id);
    }

    @Test
    void shouldDeleteCategoryWhenItExists() {
        Long id = 1L;
        when(categoryRepository.existsById(id)).thenReturn(true);

        categoryService.deleteCategory(id);

        verify(categoryRepository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentCategory() {
        Long id = 99L;
        when(categoryRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.deleteCategory(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found: " + id);

        verify(categoryRepository, never()).deleteById(any());
    }
}