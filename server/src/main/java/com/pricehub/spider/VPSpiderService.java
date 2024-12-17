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
public class VPSpiderService{

    static String url = "https://category.vip.com/suggest.php?keyword=";

    public List<Item> getItems(String input) throws InterruptedException {
        List<Item> res_item = new ArrayList<>();
        String whole_url = url + input;

        // 使用Selenium动态渲染页面
        WebDriver driver = null;
        // 设置EdgeDriver的路径
        // String edgeDriverPath = System.getProperty("user.dir") + "\\src\\main\\resources\\msedgedriver.exe";
        // System.setProperty("webdriver.edge.driver", edgeDriverPath);
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");


        // 创建EdgeOptions对象
        // EdgeOptions options = new EdgeOptions();
        // options.addArguments("--headless"); // 无头模式，不打开浏览器窗口
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        // 创建WebDriver实例
        // driver = new ChromeDriver(options);
                // 创建 ChromeDriver 服务并指定端口
        ChromeDriverService service = new ChromeDriverService.Builder()
            .usingPort(9516)  // 指定端口
            .build();

        // System.out.println("Service created");
        // WebDriver driver = null;

   
        // // 创建 WebDriver 实例，传入已配置的服务和选项
        // System.out.println("Creating driver");
        // driver = new ChromeDriver(service, options);
        // System.out.println("Driver created");

        // // 打开目标 URL
        // driver.get(fullUrl);
        // System.out.println("URL opened");

        // // 等待页面加载完成
        // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".mt20.over_hide.w_1024.offer_list > div")));

        // // 获取渲染后的页面源代码
        // String renderedHtml = driver.getPageSource();
        // driver.quit();

        // // 使用 Jsoup 解析 HTML
        // Document document = Jsoup.parse(renderedHtml);
        // Elements elements = document.select(".mt20.over_hide.w_1024.offer_list > div");

        // 打开目标URL
        driver = new ChromeDriver(service, options);
        driver.get(whole_url);
        // 等待页面加载完成（可以根据需要调整等待时间）
        Thread.sleep(10000); // 等待5秒
        System.out.println("sleep finish");
        // 获取渲染后的页面源代码
        String renderedHtml = driver.getPageSource();
        System.out.println("get");
        // 使用Jsoup解析渲染后的HTML
        Document document = Jsoup.parse(renderedHtml);
        // Document document = Jsoup.connect(whole_url).get();
        // LocalDateTime time = LocalDateTime.now();
        System.out.println(document);
        Elements all_items = document.getElementsByClass("c-goods-item");
        for (Element element : all_items) {
            String pict = element.select(".c-goods-item__img img.lazy").attr("data-original");
            if (pict.isEmpty()) {
                Element pictElement = element.getElementsByTag("img").first();
                pict = pictElement != null ? pictElement.attr("src") : "unknown";
            }
            Element nameElement = element.getElementsByClass("c-goods-item__name  c-goods-item__name--two-line").first();
            String name = nameElement != null ? nameElement.text() : "unknown";
            Element priceElement = element.getElementsByClass("c-goods-item__sale-price J-goods-item__sale-price").first();
            String price = priceElement != null ? priceElement.text().substring(1) : "unknown";
            if (name.equals("unknown") || price.equals("unknown") || pict.equals("unknown"))
                continue;
            Item item = new Item();
            item.setItemName(name);
            item.setPrice(Double.parseDouble(price));
            // item.setItem_time(time);
            item.setPlatform("唯品会");
            item.setImage(pict);
            res_item.add(item);
           System.out.println("------------------");
           System.out.println(pict);
           System.out.println(name);
           System.out.println(price);
        }
        return res_item;
    }
}