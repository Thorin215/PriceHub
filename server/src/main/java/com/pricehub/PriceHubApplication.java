package com.pricehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // 启用异步处理
public class PriceHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(PriceHubApplication.class, args);
    }
}
