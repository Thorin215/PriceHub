package com.pricehub;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRecordRepository extends JpaRepository<CodeRecord, Long> {
    CodeRecord findByUserId(String userId); // 根据用户ID查找验证码记录
}