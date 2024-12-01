package com.pricehub;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

@Service
public class SpiderService {

    private static final String URL = "https://p4psearch.1688.com/page.html?hpageId=old-sem-pc-list&spm=a2638t.b_78128457.szyxhead.submit&exp=offerWwClick%3AA&keywords=";
    private static final Logger logger = Logger.getLogger(SpiderService.class.getName());

    public List<Item> getItems(String query) throws InterruptedException, IOException {
        List<Item> items = new ArrayList<>();
        String fullUrl = URL + query;
        System.out.println("Full URL: " + fullUrl);
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        // 使用 WebDriverManager 自动管理 Chrome WebDriver
        // WebDriverManager.chromedriver().setup();
        // WebDriverManager.chromedriver().setup();
        // 获取并打印 chromedriver 路径
        String chromedriverPath = System.getProperty("webdriver.chrome.driver");
        System.out.println("ChromeDriver Path: " + chromedriverPath);

        // 配置 Chrome 浏览器选项
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        // 创建 ChromeDriver 服务并指定端口
        ChromeDriverService service = new ChromeDriverService.Builder()
            .usingPort(9515)  // 指定端口
            .build();

        System.out.println("Service created");
        WebDriver driver = null;

        try {
            // 创建 WebDriver 实例，传入已配置的服务和选项
            System.out.println("Creating driver");
            driver = new ChromeDriver(service, options);
            System.out.println("Driver created");

            // 打开目标 URL
            driver.get(fullUrl);
            System.out.println("URL opened");

            // 等待页面加载完成
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".mt20.over_hide.w_1024.offer_list > div")));

            // 获取渲染后的页面源代码
            String renderedHtml = driver.getPageSource();
            driver.quit();

            // 使用 Jsoup 解析 HTML
            Document document = Jsoup.parse(renderedHtml);
            Elements elements = document.select(".mt20.over_hide.w_1024.offer_list > div");

            // 采用线程池提高爬取效率
            ExecutorService executorService = Executors.newFixedThreadPool(12);

            // 遍历商品元素并提取数据
            for (Element element : elements) {
                executorService.submit(() -> {
                    try {
                        String img = element.select(".main-img.layLoad").attr("src");
                        String name = element.select(".offer-title").text();
                        String price = element.select(".offer-price").text();

                        if (img != null && !img.isEmpty() && name != null && !name.isEmpty() && price != null && !price.isEmpty()) {
                            String cleanedPrice = price.replaceAll("[^0-9.]", "").trim();
                            try {
                                Double parsedPrice = Double.valueOf(String.format("%.2f", Double.parseDouble(cleanedPrice)));
                                Item item = new Item();
                                item.setItemName(name);
                                item.setPrice(parsedPrice);
                                item.setImage(img);
                                item.setPlatform("阿里巴巴");

                                // 加入到结果列表
                                synchronized (items) {
                                    items.add(item);
                                }
                            } catch (NumberFormatException e) {
                                logger.warning("价格格式错误: " + price + " 商品名称: " + name);
                            }
                        }
                    } catch (Exception e) {
                        logger.warning("处理商品元素时发生错误: " + e.getMessage());
                    }
                });
            }

            // 关闭线程池并等待任务完成
            executorService.shutdown();
            executorService.awaitTermination(30, TimeUnit.SECONDS);

        } catch (WebDriverException e) {
            logger.severe("爬虫执行时发生错误: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit(); // 确保资源释放
            }
        }
        return items;
    }
}
