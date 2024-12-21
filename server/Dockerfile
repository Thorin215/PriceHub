# 使用官方的 OpenJDK 镜像作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 将构建的 JAR 文件复制到容器中的工作目录
COPY ./target/pricehub-backend-1.0-SNAPSHOT.jar pricehub.jar

# 将 src 目录的内容复制到容器中的 /app/src 目录
COPY src /app/src

# 暴露 8080 端口
EXPOSE 8080

# 启动 Spring Boot 应用
CMD ["java", "-jar", "pricehub.jar"]