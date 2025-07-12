# --- Стадия 1: Сборка приложения с помощью Gradle ---
FROM gradle:8.5.0-jdk17-alpine AS builder

# Устанавливаем рабочую директорию в контейнере
WORKDIR /app

# Копируем файлы сборки для кэширования зависимостей
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Копируем исходный код
COPY src ./src

# Собираем приложение. --no-daemon рекомендуется для CI/CD и Docker
RUN gradle build --no-daemon

# --- Стадия 2: Создание легковесного образа для запуска ---
FROM eclipse-temurin:17-jre-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем только собранный .jar файл из стадии сборки
COPY --from=builder /app/build/libs/*.jar app.jar

# Указываем порт, на котором будет работать приложение
EXPOSE 8080

# Команда для запуска приложения при старте контейнера
ENTRYPOINT ["java", "-jar", "app.jar"]
