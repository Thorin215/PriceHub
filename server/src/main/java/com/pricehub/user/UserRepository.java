package com.pricehub;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    // 根据用户ID查询用户
    Optional<User> findById(String id);
    
    // 根据用户邮箱查询用户
    Optional<User> findByEmail(String email);
}