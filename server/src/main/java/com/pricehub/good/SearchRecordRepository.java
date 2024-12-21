package com.pricehub;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRecordRepository extends JpaRepository<SearchRecord, Long> {
    List<SearchRecord> findByUserId(String userId); // 根据用户ID查找搜索记录

    SearchRecord findByUserIdAndProductName(String userId, String productName); // 根据用户ID和商品名称查找记录
}