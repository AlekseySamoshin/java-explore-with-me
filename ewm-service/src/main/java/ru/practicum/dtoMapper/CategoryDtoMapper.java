package ru.practicum.dtoMapper;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.model.Category;

public class CategoryDtoMapper {
    public Category mapNewDtoToCategory(NewCategoryDto categoryDto) {
        return Category.builder()
                .id(null)
                .name(categoryDto.getName())
                .build();
    }

    public CategoryDto mapCategoryToDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
