# 1. フロントエンドのビルド
FROM node:20 AS frontend-build
WORKDIR /app/frontend
COPY frontend/test-React/package*.json ./
# overridesを消したので、これで通るはずです
RUN npm install --legacy-peer-deps
COPY frontend/test-React/ ./
RUN npm run build

# 2. バックエンドのビルド
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# Viteはデフォルトで 'dist' フォルダに書き出すので、それをJavaへコピー
COPY --from=frontend-build /app/frontend/dist/ /app/src/main/resources/static/
RUN mvn clean package -DskipTests

# 3. 実行
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]