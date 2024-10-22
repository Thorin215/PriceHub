package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goods")
public class GoodController {

    @Autowired
    private GoodService goodService;

    // 创建商品
    @PostMapping
    public ResponseEntity<Good> createGood(@RequestBody Good good) {
        Good createdGood = goodService.createGood(good);
        return new ResponseEntity<>(createdGood, HttpStatus.CREATED);
    }

    // 获取所有商品
    @GetMapping
    public ResponseEntity<List<Good>> getAllGoods() {
        List<Good> goods = goodService.getAllGoods();
        return new ResponseEntity<>(goods, HttpStatus.OK);
    }

    // 根据ID获取商品
    @GetMapping("/{id}")
    public ResponseEntity<Good> getGoodById(@PathVariable Long id) {
        Good good = goodService.getGoodById(id);
        return new ResponseEntity<>(good, HttpStatus.OK);
    }

    // 更新商品
    @PutMapping("/{id}")
    public ResponseEntity<Good> updateGood(@PathVariable Long id, @RequestBody Good good) {
        good.setId(id); // 确保ID一致
        Good updatedGood = goodService.updateGood(good);
        return new ResponseEntity<>(updatedGood, HttpStatus.OK);
    }

    // 删除商品
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGood(@PathVariable Long id) {
        goodService.deleteGood(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 创建商品版本
    @PostMapping("/{goodId}/versions")
    public ResponseEntity<Version> createVersion(@PathVariable Long goodId, @RequestParam Double price) {
        Version version = goodService.createVersionForGood(goodId, price);
        return new ResponseEntity<>(version, HttpStatus.CREATED);
    }
}

