package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodService {

    @Autowired
    private GoodRepository goodRepository;

    @Autowired
    private VersionService versionService; // 注入 VersionService

    // 创建商品
    public Good createGood(Good good) {
        return goodRepository.save(good);
    }

    // 获取所有商品
    public List<Good> getAllGoods() {
        return goodRepository.findAll();
    }

    // 根据 ID 获取商品
    public Good getGoodById(Long id) {
        return goodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Good not found")); // 处理商品未找到的情况
    }

    // 更新商品
    public Good updateGood(Good good) {
        // 检查商品是否存在
        if (!goodRepository.existsById(good.getId())) {
            throw new RuntimeException("Good not found"); // 处理商品未找到的情况
        }
        return goodRepository.save(good);
    }

    // 删除商品
    public void deleteGood(Long id) {
        if (!goodRepository.existsById(id)) {
            throw new RuntimeException("Good not found"); // 处理商品未找到的情况
        }
        goodRepository.deleteById(id);
    }

    // 创建商品版本
    public Version createVersionForGood(Long goodId, Double price) {
        return versionService.createVersion(goodId, price);
    }
}