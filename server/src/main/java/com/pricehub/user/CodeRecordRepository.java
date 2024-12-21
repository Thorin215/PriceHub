package com.pricehub;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRecordRepository extends JpaRepository<CodeRecord, Long> {
    CodeRecord findByEmail(String email); // 根据 email 查找验证码记录
}