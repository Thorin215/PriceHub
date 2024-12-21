package com.pricehub;

import javax.persistence.*;
import java.util.Date;

@Entity
public class CodeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键

    @Column(nullable = false)
    private String email; // 替换 userId 为 email

    @Column(nullable = false)
    private String code; // 验证码

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt; // 创建时间

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // 检查验证码是否过期（假设验证码有效期为5分钟）
    public boolean isExpired() {
        Date now = new Date();
        long expirationTime = createdAt.getTime() + 5 * 60 * 1000; // 5分钟
        return now.getTime() > expirationTime;
    }
}