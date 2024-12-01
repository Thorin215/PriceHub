package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class GoodService {

    @Autowired
    private GoodRepository goodRepository;

    @Autowired
    private VersionService versionService; // 注入 VersionService

    @Autowired
    private SpiderService spiderService; // 注入 SpiderService


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

    // 根据名字爬取商品信息并更新数据库
    public void updateGoodByName(String name) {
        try {
            List<Item> items = spiderService.getItems(name);
            for (Item item : items) {
                // 检查数据库是否已存在该商品
                Good existingGood = goodRepository.findByAllFields(item.getItemName(), item.getItemName(), item.getPlatform());
                if (existingGood != null) {
                    createVersionForGood(existingGood.getId(), item.getPrice());
                } else {
                    // 创建新商品
                    Good newGood = new Good();
                    newGood.setName(item.getItemName());
                    newGood.setDescription(item.getItemName());
                    newGood.setPlatform(item.getPlatform());
                    newGood.setImage(item.getImage());
                    goodRepository.save(newGood);
                    createVersionForGood(newGood.getId(), item.getPrice());
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch and update goods: " + e.getMessage(), e);
        }
    }

    // 创建商品版本
    public Version createVersionForGood(Long goodId, Double price) {
        return versionService.createVersion(goodId, price);
    }
}