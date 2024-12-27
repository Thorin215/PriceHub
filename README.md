## Environment

- **Development Environment**: Ubuntu 22.04 (WSL)
- **Development Framework**: Vue + Java
- **Frontend Startup**: `npm run dev`
- **Backend Startup**: `mvn spring-boot:run`

## Docker

```bash
docker load < my_image_export.tar
```

### Server

- dockerfile is in `./server`

```bash
mvn clean package
docker build -t pricehub.server:latest .
```

### Web

- dockerfile is in `./web`

```bash
npm run build:prod
docker build -t pricehub.web:latest .
```

```bash
docker run -d -p 8000:80 --name pricehub.web pricehub.web:latest
```

### Access

- Can be accessed on the DockerHub

- [Web Docker](https://hub.docker.com/r/thorin215/pricehub.web)

- [Server Docker](https://hub.docker.com/r/thorin215/pricehub.server)

```bash
docker pull thorin215/pricehub.web:latest
docker pull thorin215/pricehub.server:latest
```

## Docker-Compose

```bash
./start.sh
./stop.sh
```

```bash
docker-compose up -d
```

## Docs

- All the **Documents** are inthe `./docs`，In the design report, I have incorporated the user manual, test report, development reflections, and summary。
