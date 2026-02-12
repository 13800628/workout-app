# 1. フロントエンドのビルド
FROM node:20 AS frontend-build
WORKDIR /app/frontend
# フォルダ名が 'frontend' ではない場合は適宜書き換えてください
COPY frontend/test-React/package*.json ./
RUN npm install --force
COPY frontend/ ./
RUN npm run build

# 2. バックエンドのビルド（フロントの成果物を詰め込む）
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# ステージ1で作った成果物を Java の静的リソースフォルダにコピー
# Viteなら 'dist'、Create React Appなら 'build' になっているはずです
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static
RUN mvn clean package -DskipTests

# 3. 実行
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]