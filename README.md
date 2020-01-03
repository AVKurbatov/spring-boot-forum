# Simple forum for chatting

## Overview

A simple forum project for education.

The back-end of the forum can work in two modes: with h2 and with redis. 
You need installed redis server on you PC to run the forum in the redis mode. 
However redis-dao tests run with embedded redis. 

Used technologies: Spring boot, Spring security, Spring MVC, JS, Thymeleaf, aspects,
unit-tests, sql, h2, redis, maven.

## Build

To build the forum run:

```
mvn package
```

## Run

To start the forum with h2 run:

```
java -jar target/spring_boot_forum-1.0.jar -Dspring.profiles.active=h2
```

To start with redis run:

```
java -jar target/spring_boot_forum-1.0.jar -Dspring.profiles.active=redis
```

The forum will start on
```
http://localhost:8080/
```

Users can browse topics and messages without logging.
But they need be logged in to write messages.

# Простой форум

## Обзор

Простой форму, сделанный из образованых соображений.

Бек-энд форума мжет работать в двух режимах: с h2 и с redis. 
Чтобы запустить в режиме работы с редисом, необходимо иметь redis server на вашем ПК. 
Оджнако, для запуска авто-тестов он не нужен: методы redis-dao тестируются при поомщи встроенного сервера. 

Использованы технологии: Spring boot, Spring security, Spring MVC, JS, Thymeleaf, aspects,
unit-tests, sql, h2, redis, maven.

## Сборка

Чтобы собрать проект используется строка:

```
mvn package
```

## Запуск

Для запуска в режиме работы с h2:

```
java -jar target/spring_boot_forum-1.0.jar -Dspring.profiles.active=h2
```

Для запуска с redis:

```
java -jar target/spring_boot_forum-1.0.jar -Dspring.profiles.active=redis
```

Интерфейс форума доступен по адресу

```
http://localhost:8080/
```

Пользователи могут просматривать сообщения без входа.
Однако, чтобы писать сообщения, им нужно залогиниться.