job4j_cars
Описание

Проект job4j_cars — веб-приложение для публикации объявлений о продаже автомобилей.
Позволяет создавать, редактировать, просматривать и удалять объявления с информацией о машине, фотографиями и историей изменения цены.
Технологии

    Java 17+

    Spring Framework (Spring MVC, Spring Boot)

    Hibernate ORM (JPA)

    PostgreSQL

    Thymeleaf

    Maven

    Liquibase

    Jackson

    JaCoCo

Функционал

    Регистрация и авторизация пользователей (если реализовано)

    CRUD операции с объявлениями (Post)

    Управление автомобилями (Car), марками (Mark), моделями (Model), двигателями (Engine)

    Загрузка и отображение фотографий к объявлениям

    История изменения цены с хранением timestamp и значений

    Валидация данных на сервере

    Разграничение доступа к редактированию/удалению по авторству объявления

Структура проекта

    model — сущности JPA: Post, Car, Mark, Model, Engine, PriceHistory, Photo, User

    repository — интерфейсы DAO для доступа к базе данных с помощью Hibernate

    service — бизнес-логика приложения

    controller — контроллеры (PostController и др.)

    dto — объекты для передачи данных (например, PhotoDto)

    resources/db/changelog — Liquibase миграции для создания и обновления структуры БД

    templates — Thymeleaf шаблоны для веб-интерфейса

    static — CSS, JS и ресурсы фронтенда

Установка и запуск

    Клонировать репозиторий:
```
git clone https://github.com/yourusername/job4j_cars.git
cd job4j_cars
```

Настроить базу данных PostgreSQL:

    Создать базу (например, job4j_cars)

    Обновить настройки подключения в hibernate.cfg.xml

Запустить миграции Liquibase (автоматически при старте приложения или вручную)

Собрать и запустить приложение:
```
mvn clean install
mvn spring-boot:run
```

Открыть в браузере:
```
    http://localhost:8080/
```

Основные эндпоинты

    GET / — главная страница с списком объявлений

    GET /post/{id} — просмотр объявления

    GET /post/create — страница создания объявления

    POST /post/save — сохранение нового объявления

    GET /post/update/{id} — страница редактирования объявления

    POST /post/update — обновление объявления

    POST /post/delete/{id} — удаление объявления

    GET /photo/{id} — получение фотографии по ID

    POST /photo/upload — загрузка фотографий к объявлению

Особенности реализации

    Цена и история изменений: при изменении цены создается запись в таблице price_history, отображаемая в интерфейсе.

    Загрузка фото: фотографии хранятся в базе или на диске (в зависимости от конфигурации), выводятся в объявлении.

    Безопасность: проверка авторства перед редактированием и удалением.

Советы и рекомендации по развитию

    Добавить полноценную регистрацию и аутентификацию пользователей через Spring Security.

    Реализовать пагинацию и фильтрацию объявлений.

    Добавление подписчиков на пост (будет реализовано позже, но можете реализовать уже сейчас при желании). Необходимые таблицы и связи в сущностях уже имеются

    Оптимизировать загрузку фото и кеширование.
