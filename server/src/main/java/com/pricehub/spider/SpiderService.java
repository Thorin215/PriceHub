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
import org.openqa.selenium.remote.RemoteWebDriver;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;
import java.net.URL;
import org.openqa.selenium.WebDriverException;
import java.util.Collections;
import org.openqa.selenium.JavascriptExecutor;

@Service
public class SpiderService {

    private static final String URL = "https://p4psearch.1688.com/page.html?hpageId=old-sem-pc-list&spm=a2638t.b_78128457.szyxhead.submit&exp=offerWwClick%3AA&keywords=";
    private static final Logger logger = Logger.getLogger(SpiderService.class.getName());
    private static final String SUNING_URL = "https://search.suning.com/";
    public List<Item> getItems(String query) throws InterruptedException, IOException {
        List<Item> items = new ArrayList<>();
        String fullUrl = URL + query;
        System.out.println("Full URL: " + fullUrl);
        // System.setProperty("webdriver.chrome.driver", "/app/src/main/resources/chromedriver");

        // String chromedriverPath = System.getProperty("webdriver.chrome.driver");
        // System.out.println("ChromeDriver Path: " + chromedriverPath);

        // 配置 Chrome 浏览器选项
        // ChromeOptions options = new ChromeOptions();
        // // options.setBinary("/app/src/main/resources/chromium-browser");
        // options.addArguments("--remote-allow-origins=*");
        // options.addArguments("--headless");
        // options.addArguments("--disable-gpu");
        // options.addArguments("--no-sandbox");

        // System.out.println("Options created");
        // 创建 ChromeDriver 服务并指定端口
        // ChromeDriverService service = new ChromeDriverService.Builder()
        //     .usingPort(9515)  // 指定端口
        //     .build();

        // System.out.println("Service created");
        // WebDriver driver = null;
        
        // String seleniumHubUrl = "http://selenium-chrome:4444"; // Docker Desktop 可以用 host.docker.internal

        // // 如果你知道你的容器的 IP 地址，也可以使用 IP
        // // String seleniumHubUrl = "http://<container-ip>:4444/wd/hub";

        // System.out.println("Connecting to Selenium Hub: " + seleniumHubUrl);
        // // System.out.println("URL created");

        // WebDriver driver = null;
        // WebDriver driver = new RemoteWebDriver(url, options);
        // System.out.println("Driver created");
        /*
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");


        // System.out.println("Options created");
        // 创建 ChromeDriver 服务并指定端口
        ChromeDriverService service = new ChromeDriverService.Builder()
            .usingPort(9515)  // 指定端口
            .build();

        WebDriver driver =  new ChromeDriver(service, options);
        */
        
        WebDriver driver = createWebDriver();
        try {
            /*Docker Version*/

            // URL url = new URL(seleniumHubUrl); // 连接到 Selenium Grid
            // System.out.println("URL created");
            // try{
            //     driver = new RemoteWebDriver(url, options);
            //     System.out.println("Driver created");
            // }catch(WebDriverException e){
            //     System.out.println("Error: " + e.getMessage());
            // }
            // driver = new RemoteWebDriver(url, options);
            // System.out.println("Driver created");
            
            driver.get(fullUrl);
            System.out.println("URL opened");
            // 创建 WebDriver 实例，传入已配置的服务和选项
            // System.out.println("Creating driver");
            // // driver = new ChromeDriver(service, options);



            // System.out.println("Driver created");

            // // 打开目标 URL
            // driver.get(fullUrl);
            // System.out.println("URL opened");

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

    public List<Item> getItemsSN(String query) throws IOException {
        try {
            // WebDriver driver = createWebDriver();
            System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");


            // System.out.println("Options created");
            // 创建 ChromeDriver 服务并指定端口
            ChromeDriverService service = new ChromeDriverService.Builder()
                .usingPort(9515)  // 指定端口
                .build();

            WebDriver driver =  new ChromeDriver(service, options);

            driver.get(SUNING_URL + query + "/");

            // 滚动到底部以加载更多内容
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(2000); // 或者使用显式等待

            // 获取页面源代码
            String pageSource = driver.getPageSource();
            driver.quit();

            // 使用 Jsoup 解析 HTML
            Document document = Jsoup.parse(pageSource);
            Element element = document.getElementById("product-list");
            if (element == null) return Collections.emptyList();

            Elements elements = element.getElementsByTag("li");
            List<Item> items = new ArrayList<>();

            // 创建线程池
            ExecutorService executorService = Executors.newFixedThreadPool(10); // 设置线程池大小

            // 遍历商品元素并提取数据
            for (Element productElement : elements) {
                executorService.submit(() -> {
                    try {
                        // 商品名称详情链接获取
                        Element nameElement = productElement.getElementsByClass("title-selling-point").first();
                        if (nameElement == null) return;
                        Element subNameElement = nameElement.getElementsByTag("a").first();
                        if (subNameElement == null) return;
                        String name = subNameElement.text();

                        String productURL = subNameElement.attr("href");
                        if (productURL.startsWith("https")) return;
                        productURL = "https:" + productURL.replace("https", "http");

                        // 图片获取
                        Element photoElement = productElement.getElementsByClass("res-img").first();
                        Element subPhotoElement = photoElement.getElementsByTag("img").first();
                        String photoURL = subPhotoElement.attr("src");

                        // 价格获取
                        Element priceElement = productElement.getElementsByClass("def-price").first();
                        if (priceElement == null) return;
                        String price = priceElement.text();
                        if (price.matches("^\\s*$")) return; // 是否没有加载出来
                        price = price.replaceAll("[^0-9.]", ""); // 使用正则表达式取出非数字部分


                        // 创建 Item 对象
                        Item item = new Item();
                        item.setItemName(name);
                        item.setPrice(Double.parseDouble(price));
                        item.setImage(photoURL);
                        item.setPlatform("苏宁");

                        // 同步添加到结果列表
                        synchronized (items) {
                            items.add(item);
                        }
                    } catch (Exception e) {
                        logger.warning("处理商品元素时发生错误: " + e.getMessage());
                    }
                });
            }

            // 关闭线程池并等待所有任务完成
            executorService.shutdown();
            executorService.awaitTermination(30, TimeUnit.SECONDS);

            System.out.println("苏宁关键词：" + query + "爬取成功, 共计" + items.size() + "项");
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private WebDriver createWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        String seleniumHubUrl = "http://selenium-chrome:4444";
        try {
            URL url = new URL(seleniumHubUrl);
            return new RemoteWebDriver(url, options);
        } catch (Exception e) {
            logger.severe("无法连接到 Selenium Hub: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
