package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/goods")
@CrossOrigin(origins = "*")
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
    @PostMapping("/all")
    public ResponseEntity<ListResponse> getAllGoods() {
        List<Good> goods = goodService.getAllGoods();
        ListResponse response = new ListResponse("成功", 200, goods);
        return new ResponseEntity<>(response, HttpStatus.OK);
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

    // @PostMapping("/update")
    // public ResponseEntity<ListResponse> updateGoodByName(@RequestParam String name) {
    //     List<Good> goods = null;
    //     try {
    //         goodService.updateGoodByName(name);
    //         // 返回成功状态和消息
    //         ListResponse response = new ListResponse("成功", 200, goods);
    //         return new ResponseEntity<>(response, HttpStatus.OK);
    //     } catch (Exception e) {
    //         // 返回错误状态和消息
    //         ListResponse response = new ListResponse("失败", 500, goods);
    //         return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    //     }
    // }
    
    @PostMapping("/update")
    public ResponseEntity<ListResponse> updateGoodByName(@RequestParam String name) {
        try {
            // 调用服务层方法
            goodService.updateGoodByNameAsync(name);

            // 构建成功响应
            ListResponse response = new ListResponse("成功", 200, Collections.emptyList());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // 构建错误响应
            ListResponse response = new ListResponse("失败: " + e.getMessage(), 500, Collections.emptyList());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 根据名字搜索商品
    @GetMapping("/search")
    public ResponseEntity<ListResponse> searchGoodsByName(@RequestParam String name) {
        List<Good> goods = goodService.searchGoodsByName(name);
        ListResponse response = new ListResponse("成功", 200, goods);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 创建商品版本
    // @PostMapping("/{goodId}/versions")
    // public ResponseEntity<Version> createVersion(@PathVariable Long goodId, @RequestParam Double price) {
    //     Version version = goodService.createVersionForGood(goodId, price);
    //     return new ResponseEntity<>(version, HttpStatus.CREATED);
    // }

    // ListResponse 静态类
    static class ListResponse { 
        private String msg;
        private int code;
        private List<Good> data; 

        public ListResponse(String msg, int code, List<Good> data) {
            this.msg = msg;
            this.code = code;
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public List<Good> getData() {
            return data;
        }

        public void setData(List<Good> data) {
            this.data = data;
        }
    }
}

