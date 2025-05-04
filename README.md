# OTPService

Сервис для отправки и верификации одноразовых паролей (OTP) через различные каналы связи.

## Описание:

OTPService - это Spring Boot приложение, которое предоставляет API для:
- Генерации и отправки OTP через SMS (SMPP), Telegram и Email
- Верификации OTP
- Управления пользователями и их правами доступа

## Технологии:

- Java 17
- Spring Boot 3.4.4
- Spring Security
- Spring Data JPA
- PostgreSQL
- SMPP (OpenSMPP)
- Telegram Bot API
- JWT для аутентификации
- Swagger/OpenAPI для документации API

## Требования:

- Java 17 или выше
- PostgreSQL
- Локальный SMPP сервер
- Docker или Docker Compose (опционально)

## Установка и запуск:

1. Клонируйте репозиторий:
```bash
git clone https://github.com/your-username/OTPService.git
cd OTPService
```

2. Запустите базу данных через Docker Compose:
```bash
docker-compose up -d postgres
```

3. Настройте Telegram бота:
- Перейдите по ссылке: [@mHide_otpBot](https://t.me/mHide_otpBot)
- Напишите команду `/start` боту, чтобы активировать его
- Получите chatId из ответа бота

4. Соберите и запустите проект:
```bash
# Сборка проекта
./gradlew build

# Запуск приложения
./gradlew bootRun
```

## Тестирование API

### 1. Регистрация пользователя:
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "role": "USER"
    "email": "test@example.com",
    "phoneNumber": "+79991234567",
    "telegramId": 123456
  }'
```

### 2. Вход в систему:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 3. Отправка OTP:
```bash
curl -X POST http://localhost:8080/otp \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "operationId": 123,
  }'
```

### 4. Верификация OTP
```bash
curl -X POST http://localhost:8080/otp/validate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "code": "123456",
    "operationId": 123,
  }'
```

## Конфигурация

### База данных
База данных PostgreSQL запускается автоматически через Docker Compose. Настройки по умолчанию:
- Порт: 5432
- Пользователь: user
- Пароль: password
- База данных: postgres

### SMPP сервер:
- Локальный SMPP сервер должен быть запущен
- Настройки подключения в `application.yaml`

### Почтовый сервер:
- Локальный почтовый сервер запускается автоматически

### Telegram бот:
- Бот доступен по ссылке: [@mHide_otpBot](https://t.me/mHide_otpBot)
- После запуска приложения необходимо:
  1. Найти бота в Telegram
  2. Отправить команду `/start`
  3. Сохранить полученный chatId для отправки OTP

## Документация API

После запуска приложения, документация API доступна по адресу:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI спецификация: `http://localhost:8080/v3/api-docs`
