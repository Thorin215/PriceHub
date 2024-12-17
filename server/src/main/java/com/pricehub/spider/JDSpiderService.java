package com.pricehub;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class JDSpiderService {

    static String url = "https://search.jd.com/Search?keyword=";
    static Map<String, String> cookies = new HashMap<String, String>();

    public List<Item> getItems(String input) throws IOException, InterruptedException {
        System.out.println("input: " + input);
        List<Item> resItem = Collections.synchronizedList(new ArrayList<>());
        cookies.put("thor", "F83DC28F0CDE6767E77F5037040DF623C4F83D452FFC7C782FFCB1D0801659F2F25F1A9D0CBF5C567D1EF682E846EA525184A2598FCEBBA221C9B3A3A81ACB35B3CF7BE6C93D9824100A2AF8AF8265BA4ACE4018A7DE9C086E8EAE7B925B8443D789BA798E79E9DC729C004F5D9FB46B967327404B7334D2A4A0EFBA230B0AE50374E2B6F50767CAEA50C4D9C1886FF56141B16466A43455D1AB55716630B107");
        try{
            // 获取网页内容
            String wholeUrl = url + input;
            Document document = Jsoup.connect(wholeUrl).header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3").cookies(cookies).get();
            System.out.println(document);
            Elements ul = document.getElementsByClass("gl-warp clearfix");
            Elements elements = ul.select("li");
            System.out.println(elements);
            // 创建线程池
            ExecutorService executorService = Executors.newFixedThreadPool(10); // 可调整线程数量

            for (Element element : elements) {
                executorService.submit(() -> {
                    try {
                        String pict = element.getElementsByTag("img").first() != null
                                ? element.getElementsByTag("img").first().attr("data-lazy-img")
                                : "unknown";

                        String name = element.getElementsByClass("p-name").select("em").text();
                        String priceText = element.getElementsByClass("p-price").text().substring(1).replaceAll("[^\\d.]", "");
                        
                        if (!name.isEmpty() && !priceText.isEmpty() && pict != null && !pict.equals("unknown")) {
                            try{
                                double price = Double.valueOf(String.format("%.2f", Double.parseDouble(priceText)));

                                // 创建并添加商品信息
                                Item item = new Item();
                                item.setItemName(name);
                                item.setPrice(price);
                                item.setPlatform("京东");
                                item.setImage(pict);

                                synchronized (resItem) {
                                    resItem.add(item);
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("价格格式错误: " + priceText + " 商品名称: " + name);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing element: " + e.getMessage());
                    }
                });
            }

            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.err.println("线程等待中断：" + e.getMessage());
                Thread.currentThread().interrupt(); // 恢复线程中断状态
            }
        } catch (IOException e) {
            System.err.println("爬虫执行时发生错误: " + e.getMessage());
        }
        System.out.println("JD OUT");
        return resItem;
    }
}
