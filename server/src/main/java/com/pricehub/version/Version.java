package com.pricehub;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Version {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自动递增ID
    private Long id;          // 版本ID
    private Long goodId;      // 商品ID
    private LocalDateTime createdAt; // 创建时间
    private Double price;   // 当时的价格

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

