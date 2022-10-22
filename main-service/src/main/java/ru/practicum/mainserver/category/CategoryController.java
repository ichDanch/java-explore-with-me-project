package ru.practicum.mainserver.category;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainserver.category.dto.CategoryDto;
import ru.practicum.mainserver.validation.CreateValidation;
import ru.practicum.mainserver.validation.PatchValidation;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
public class CategoryController {

    private final CategoryServiceImpl categoryService;

    public CategoryController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Admin: Категории
     * API для работы с категориями
     */

    @PostMapping("/admin/categories")
    @Validated(CreateValidation.class)
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping("/admin/categories")
    @Validated(PatchValidation.class)
    public CategoryDto patchCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.patchCategory(categoryDto);
    }

    @DeleteMapping("admin/categories/{catId}")
    public void deleteCategory(@PathVariable(name = "catId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
    }

    /**
     * Public: Категории
     * Публичный API для работы с категориями
     */

    @GetMapping("/categories")
    public List<CategoryDto> findAllCategories(@RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.findAllCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto findCategoryById(@PathVariable Long catId) {
        return categoryService.findCategoryById(catId);
    }
}