# Используем базовый образ с Java и Ubuntu
FROM openjdk:17

# Установка рабочей директории в контейнере
WORKDIR /app

# Копируем скомпилированные файлы приложения в контейнер
COPY target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

# Команда для запуска приложения Spring Boot
CMD ["java", "-jar", "app.jar"]
