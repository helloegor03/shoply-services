# Shoply Services

Shoply — микросервисное REST API приложение для электронной коммерции, построенное на Spring Boot и Spring Cloud. Позволяет управлять пользователями, товарами и заказами с использованием современной микросервисной архитектуры.  

## Архитектура
<img width="1280" height="720" alt="image" src="https://github.com/user-attachments/assets/5ead0e7d-8087-498f-8956-062b757d4011" />
- **Config Server** — централизованное хранилище конфигураций.  <br>
- **Discovery Server (Eureka)** — сервис обнаружения микросервисов.  <br>
- **Gateway** — точка входа и маршрутизация запросов.  <br>
- **Auth Service** — авторизация пользователей и генерация JWT.  <br>
- **Product Service** — управление товарами, загрузка изображений через Cloudinary.  <br>
- **Order Service** — обработка заказов, взаимодействие с Product Service через Kafka.  <br>
- **ZooKeeper & Kafka** — управление очередями и синхронизация сервисов.  <br>
- **Zipkin** — распределённое трассирование запросов.<br>

## Примеры запросов в Postman
**Регистрация пользователя:**  
`POST http://localhost:8222/auth/register`

```json
{
    "username": "testuser",
    "email": "testuser@yahoo.com",
    "password": "testpassword"
}
```
**Авторизация пользователя:**  
`POST http://localhost:8222/auth/login`
```json
{
    "username": "testuser",
    "password": "testpassword"
}
```
**Добавление нового товара:**  
`POST http://localhost:8222/product/add`
<img width="1280" height="232" alt="image" src="https://github.com/user-attachments/assets/6cd1a69b-73dd-4e49-90b5-3a4c54967ee5" />
<img width="1280" height="213" alt="image" src="https://github.com/user-attachments/assets/066fb301-f231-4811-b0aa-90647350cb96" />

**Покупка товара:**  
`http://localhost:8222/order/buy/{productId}`
```json
{
}
```

## Стэк

Java 17 | Spring Boot 3.x | Spring Cloud  | Docker | | PostgreSQL | Kafka | Cloudinary | Zipkin | Maven |  

## Как установить репозиторий

1. Клонируйте репозиторий:  
git clone https://github.com/helloegor03/shoply-services.git
2. Запустите Kafka и ZooKeeper (через Docker).
3. Настройте конфигурации через Config Server.
Запустите сервисы через IDE или Maven:


