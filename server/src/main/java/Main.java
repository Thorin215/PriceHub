package zjubs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication(scanBasePackages = {"route", "api"})
public class Main {
    public static void main(String[] args) {
        // 启动 Spring 应用程序
        ApplicationContext context = SpringApplication.run(Main.class, args);

        // 获取 RequestMappingHandlerMapping 来访问所有的请求映射
        RequestMappingHandlerMapping requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);

        // 打印每个正在运行的路由
        requestMappingHandlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
            System.out.println(requestMappingInfo);
        });
    }
}