# 1. フロントエンドのビルド
FROM node:20 AS frontend-build
WORKDIR /app/frontend

# package-lock.json もコピーするように変更（npm ci のため）
COPY frontend/test-React/package*.json ./

# 依存関係のチェックを完全に無視してインストールを強行する
RUN npm install --legacy-peer-deps

COPY frontend/test-React/ ./
RUN npm run build

# 2. バックエンドのビルド（以下、前回と同じ）
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# コピー元のパスを修正（frontend-buildステージのWORKDIRが/app/frontendなので、そこのdistを指す）
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static
RUN mvn clean package -DskipTests

# 3. 実行（以下、前回と同じ）
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]