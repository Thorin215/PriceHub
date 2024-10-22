package com.pricehub;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodRepository extends JpaRepository<Good, Long> {
    // 你可以在这里添加自定义查询方法，例如根据名称查找商品
}
