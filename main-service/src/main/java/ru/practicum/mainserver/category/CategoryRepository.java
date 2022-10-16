package ru.practicum.mainserver.category;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainserver.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
