package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // 创建类别
    public void createCategory(String name, Long parentId) {
        Category category = new Category();
        category.setName(name);
        
        // 如果有父类别，设置父类别
        if (parentId != null) {
            Category parentCategory = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parentCategory);
        }

        categoryRepository.save(category);
    }

    // 获取所有类别
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
