package com.pricehub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
public class SpiderController {

    @Autowired
    private SpiderService spiderService;

    /**
     * 获取爬虫数据
     * 
     * @param query 搜索关键词
     * @return 爬取的数据结果
     */
    @GetMapping("/api/spider")
    public Result getSpiderData(@RequestParam String query) {
        System.out.println("query: " + query);
        if (query == null || query.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query parameter cannot be empty");
        }

        try {
            List<Item> spiderResult = spiderService.getItems(query);
            return Result.success(spiderResult, spiderResult.size());
        } catch (IOException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch data", e);
        }
    }
}
