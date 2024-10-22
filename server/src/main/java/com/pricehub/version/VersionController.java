package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/versions")
public class VersionController {

    @Autowired
    private VersionService versionService;

    // // 创建版本
    // @PostMapping
    // public ResponseEntity<Version> createVersion(@RequestParam Long goodId, @RequestParam Double price) {
    //     Version version = versionService.createVersion(goodId, price);
    //     return new ResponseEntity<>(version, HttpStatus.CREATED);
    // }

    // 创建版本
    @PostMapping
    public Version createVersion(@RequestBody Version version) {
        return versionService.createVersion(version.getGoodId(), version.getPrice());
    }

    // 根据商品ID获取版本
    @GetMapping("/{goodId}")
    public ResponseEntity<List<Version>> getVersionsByGoodId(@PathVariable Long goodId) {
        List<Version> versions = versionService.getVersionsByGoodId(goodId);
        return new ResponseEntity<>(versions, HttpStatus.OK);
    }

    // 其他与版本相关的接口
}
