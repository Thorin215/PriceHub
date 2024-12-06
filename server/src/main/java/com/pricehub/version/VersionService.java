package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
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

    // 获取商品的最新版本号
    public Version getLatestVersionByGoodId(Long goodId) {
        // 获取所有与商品关联的版本
        List<Version> versions = versionRepository.findByGoodId(goodId);
        
        // 如果没有版本，返回 null
        if (versions == null || versions.isEmpty()) {
            return null;
        }
        
        // 按创建时间降序排列，获取最新的版本
        return versions.stream()
                .max(Comparator.comparing(Version::getCreatedAt)) // 按 createdAt 降序排列
                .orElse(null); // 如果列表为空，返回 null
    }
}
