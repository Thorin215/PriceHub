package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.scheduling.annotation.Async;

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

    @Autowired
    private SNSpiderService snSpiderService; // 注入 SNSpiderService

    @Autowired
    private SearchRecordRepository searchRecordRepository;

    private ExecutorService mainExecutorService;
    // 创建商品
    @PostConstruct
    public void init() {
        mainExecutorService = Executors.newFixedThreadPool(30); // 初始化固定大小的线程池
    }

    @PreDestroy
    public void shutdown() {
        if (mainExecutorService != null) {
            mainExecutorService.shutdown();
            try {
                if (!mainExecutorService.awaitTermination(1, TimeUnit.MINUTES)) {
                    mainExecutorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                mainExecutorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

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

   @Async
    public void updateGoodByNameAsync(String name) {
        if (mainExecutorService == null) {
            throw new IllegalStateException("Thread pool not initialized");
        }

        try {
            // 使用线程池提交任务
            fetchAndUpdateGoodsFromPlatform(snSpiderService, name);
            fetchAndUpdateGoodsFromPlatform(spiderService, name);
        } catch (Exception e) {
            System.err.println("Thread interrupted: " + e.getMessage());
            throw new RuntimeException("Failed to fetch and update goods due to thread interruption.", e);
        }
    }

    // 更新商品信息的方法
    private void fetchAndUpdateGoodsFromPlatform(Object spiderService, String name) {
        try {
            List<Item> items;
            if (spiderService instanceof SpiderService) {
                items = ((SpiderService) spiderService).getItems(name);
            } else if (spiderService instanceof SNSpiderService) {
                items = ((SNSpiderService) spiderService).getItems(name);
            } else {
                throw new IllegalArgumentException("Unsupported spider service");
            }

            for (Item item : items) {
                mainExecutorService.submit(() -> {
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
        } catch (IOException | InterruptedException e) {
            System.err.println("异常: " + e.getMessage());
            throw new RuntimeException("Failed to fetch and update goods.", e);
        }
    }

    // //     // 根据名字爬取商品信息并更新数据库
    // public void updateGoodByName(String name) {
    //     try {
    //         List<Item> items = spiderService.getItems(name);
    //         for (Item item : items) {
    //             // 检查数据库是否已存在该商品
    //             Good existingGood = goodRepository.findByAllFields(item.getItemName(), item.getItemName(), item.getPlatform());
    //             if (existingGood != null) {
    //                 createVersionForGood(existingGood.getId(), item.getPrice());
    //             } else {
    //                 // 创建新商品
    //                 Good newGood = new Good();
    //                 newGood.setName(item.getItemName());
    //                 newGood.setDescription(item.getItemName());
    //                 newGood.setPlatform(item.getPlatform());
    //                 newGood.setImage(item.getImage());
    //                 goodRepository.save(newGood);
    //                 createVersionForGood(newGood.getId(), item.getPrice());
    //             }
    //         }
    //     } catch (IOException | InterruptedException e) {
    //         throw new RuntimeException("Failed to fetch and update goods: " + e.getMessage(), e);
    //     }
    // }

    // @Async
    // public void updateGoodByNameAsync(String name) {
    //     try {
    //         List<Item> items = spiderService.getItems(name);
    //         for (Item item : items) {
    //             // 检查数据库是否已存在该商品
    //             Good existingGood = goodRepository.findByAllFields(item.getItemName(), item.getItemName(), item.getPlatform());
    //             if (existingGood != null) {
    //                 createVersionForGood(existingGood.getId(), item.getPrice());
    //             } else {
    //                 // 创建新商品
    //                 Good newGood = new Good();
    //                 newGood.setName(item.getItemName());
    //                 newGood.setDescription(item.getItemName());
    //                 newGood.setPlatform(item.getPlatform());
    //                 newGood.setImage(item.getImage());
    //                 goodRepository.save(newGood);
    //                 createVersionForGood(newGood.getId(), item.getPrice());
    //             }
    //         }
    //     } catch (IOException | InterruptedException e) {
    //         throw new RuntimeException("Failed to fetch and update goods: " + e.getMessage(), e);
    //     }
    // }

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

     public void saveSearchRecord(String userId, String productName) {
        // 查找是否已存在该用户的搜索记录
        SearchRecord existingRecord = searchRecordRepository.findByUserIdAndProductName(userId, productName);

        if (existingRecord != null) {
            // 如果存在，增加搜索次数
            existingRecord.setSearchCount(existingRecord.getSearchCount() + 1);
            searchRecordRepository.save(existingRecord);
        } else {
            // 如果不存在，创建新记录
            SearchRecord newRecord = new SearchRecord();
            newRecord.setUserId(userId);
            newRecord.setProductName(productName);
            newRecord.setSearchCount(1); // 初始搜索次数为1
            searchRecordRepository.save(newRecord);
        }
    }

    // 根据用户ID获取搜索记录
    public List<SearchRecord> getSearchRecordsByUserId(String userId) {
        return searchRecordRepository.findByUserId(userId);
    }
}