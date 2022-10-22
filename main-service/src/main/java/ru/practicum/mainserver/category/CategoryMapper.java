package ru.practicum.mainserver.category;

import ru.practicum.mainserver.category.dto.CategoryDto;
import ru.practicum.mainserver.category.model.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDto(category.getId(), category.getName());
    }

    public static Category toCategory(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }
        return new Category(categoryDto.getId(), categoryDto.getName());
    }
}
