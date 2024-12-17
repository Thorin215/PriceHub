package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.io.IOException;
import java.time.LocalDateTime;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Service
public class GoodService {

    @Autowired
    private GoodRepository goodRepository;

    @Autowired
    private VersionService versionService; // 注入 VersionService

    @Autowired
    private SpiderService spiderService; // 注入 SpiderService

    @Autowired
    private JDSpiderService jdSpiderService; // 注入 JDSpiderService

    private ExecutorService mainExecutorService;
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

    // 更新商品信息的方法
    public void updateGoodByName(String name) {
        if (mainExecutorService == null) {
            mainExecutorService = Executors.newCachedThreadPool(); // Initialize the thread pool
        }

        try {
            // Use thread pool to fetch and update goods from platform
            // mainExecutorService.submit(() -> fetchAndUpdateGoodsFromPlatform(spiderService, name));
            mainExecutorService.submit(() -> fetchAndUpdateGoodsFromPlatform(jdSpiderService, name));

            // Wait for all tasks to complete
            mainExecutorService.shutdown();
            if (!mainExecutorService.awaitTermination(1, TimeUnit.HOURS)) {
                mainExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted: " + e.getMessage());
            throw new RuntimeException("Failed to fetch and update goods due to thread interruption.", e);
        }
    }

    private void fetchAndUpdateGoodsFromPlatform(Object spiderService, String name) {
        ExecutorService executorService = Executors.newCachedThreadPool(); // 为每个平台创建新的线程池

        try {
            List<Item> items;
            if (spiderService instanceof SpiderService) {
                items = ((SpiderService) spiderService).getItems(name);
            } else if (spiderService instanceof JDSpiderService) {
                items = ((JDSpiderService) spiderService).getItems(name);
            } else {
                throw new IllegalArgumentException("Unsupported spider service");
            }

            for (Item item : items) {
                executorService.submit(() -> {
                    try {
                        // 检查数据库是否已存在该商品
                        Good existingGood = goodRepository.findByAllFields(item.getItemName(), item.getItemName(), item.getPlatform());
                        if (existingGood != null) {
                            createVersionForGood(existingGood.getId(), item.getPrice());
                        } else {
                            Good newGood = new Good();
                            newGood.setName(item.getItemName());
                            newGood.setDescription(item.getItemName());
                            newGood.setPlatform(item.getPlatform());
                            newGood.setImage(item.getImage());
                            goodRepository.save(newGood);
                            createVersionForGood(newGood.getId(), item.getPrice());
                        }
                    } catch (Exception e) {
                        System.err.println("处理商品 " + item.getItemName() + " 时出错: " + e.getMessage());
                    }
                });
            }

            executorService.shutdown();
            if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
                executorService.shutdownNow();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("异常: " + e.getMessage());
            throw new RuntimeException("Failed to fetch and update goods.", e);
        }
    }

    public List<Good> searchGoodsByName(String name) {
        return goodRepository.findByNameContainingIgnoreCase(name);
    }
    // 创建商品版本
    public Version createVersionForGood(Long goodId, Double price) {
        return versionService.createVersion(goodId, price);
    }

    public String searchImageById(Long goodId) {
        Good good = goodRepository.findById(goodId)
                .orElseThrow(() -> new RuntimeException("Good not found"));
        return good.getImage(); // 返回商品的图片路径
    }
}