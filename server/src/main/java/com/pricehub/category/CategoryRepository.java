package com.pricehub;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 你可以在这里添加自定义查询方法，例如根据名称查找类别
}
