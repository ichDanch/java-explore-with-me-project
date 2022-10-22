package ru.practicum.mainserver.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainserver.category.dto.CategoryDto;
import ru.practicum.mainserver.category.model.Category;
import ru.practicum.mainserver.exception.exceptions.CategoryNotFoundException;
import ru.practicum.mainserver.exception.exceptions.ValidationException;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.toDto(savedCategory);
    }

    @Transactional
    public CategoryDto patchCategory(CategoryDto categoryDto) {
        Category category = getCategory(categoryDto.getId());
        category.setName(categoryDto.getName());
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        getCategory(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    public List<CategoryDto> findAllCategories(Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new ValidationException("from or size are not valid or ids is empty" +
                    "Method [getAllUsers] class [UserServiceImpl] " + " from = " + from + " size = " + size);
        }

        Pageable pageWithElements = PageRequest.of(from / size, size, Sort.by("id"));

        Page<Category> page = categoryRepository.findAll(pageWithElements);
        return page.stream().map(CategoryMapper::toDto).collect(Collectors.toList());
    }

    public CategoryDto findCategoryById(Long categoryId) {
        Category category = getCategory(categoryId);
        return CategoryMapper.toDto(category);
    }

    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

}
