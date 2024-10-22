package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VersionService {
    
    @Autowired
    private VersionRepository versionRepository;

    // 创建新的版本
    public Version createVersion(Long goodId, Double price) {
        Version version = new Version();
        version.setGoodId(goodId);
        version.setCreatedAt(LocalDateTime.now()); // 存储当前时间
        version.setPrice(price); 
        return versionRepository.save(version);
    }

    // 根据商品ID获取版本
    public List<Version> getVersionsByGoodId(Long goodId) {
        return versionRepository.findByGoodId(goodId); // 直接调用 repository 方法
    }

    // 其他与版本相关的方法
}
