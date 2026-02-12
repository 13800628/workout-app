# 1. フロントエンドのビルド
FROM node:20 AS frontend-build
WORKDIR /app/frontend

COPY frontend/test-React/package*.json ./

# 厳格なチェックを無効化する環境変数を設定し、かつキャッシュを無視してインストール
ENV NPM_CONFIG_STRICT_PEER_DEPS=false
RUN npm install --omit=dev --legacy-peer-deps || npm install --legacy-peer-deps

COPY frontend/test-React/ ./
RUN npm run build

# 2. バックエンドのビルド（以下、前回と同じ）
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# ビルド成果物をコピー
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static
RUN mvn clean package -DskipTests

# 3. 実行（以下、前回と同じ）
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]