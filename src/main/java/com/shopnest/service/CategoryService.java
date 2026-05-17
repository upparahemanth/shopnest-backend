package com.shopnest.service;

import com.shopnest.entity.Category;
import java.util.List;

public interface CategoryService {
    Category createCategory(String name, String description);
    Category updateCategory(Long id, String name, String description);
    void deleteCategory(Long id);
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
}