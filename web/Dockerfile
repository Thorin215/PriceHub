# 使用 Node.js 16.17.1 的 Alpine 镜像作为构建阶段
FROM node:16.17.1-alpine AS web

# 设置工作目录
WORKDIR /app

# 复制 package.json 和 package-lock.json 文件
COPY package*.json ./

# 设置 npm 镜像源为 npmmirror.com，并安装依赖
RUN npm config set registry https://registry.npmmirror.com/ && npm install

# 复制项目文件到工作目录
COPY . .

# 构建前端项目
RUN npm run build:prod

# 使用 Nginx 的 Alpine 镜像作为最终阶段
FROM nginx:stable-alpine

# 复制构建阶段生成的 dist 文件到 Nginx 的默认目录
COPY --from=web /app/dist /usr/share/nginx/html

# 复制自定义的 Nginx 配置文件
COPY default.conf /etc/nginx/conf.d/default.conf

# 暴露 80 端口
EXPOSE 80

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]