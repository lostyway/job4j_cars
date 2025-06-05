# ARCHITECTURE.md

## Цели проекта
- Пользователь регистрируется/логинится.
- Он может создать объявление о продаже машины: выбрать название, категорию, марку, модель, указать модель, год, цену, описание, загрузить фото.
- Он может посмотреть каталог всех объявлений (с возможностью фильтрации по категориям, страничного вывода).
- Только владелец объявления может пометить его как «продано».
- Только владелец может редактировать или удалять своё объявление.

## Слои и пакеты
1. ru.job4j.cars.config
   - HibernateConfig.java — бин SessionFactory.
2. ru.job4j.cars.model
   - User, Engine, Car, Post, Owner, PriceHistory, 
3. ru.job4j.cars.repository
   - AbstractCrudRepository<T, ID>, UserRepository, PostRepository, CarRepository
4. ru.job4j.cars.service
   - UserService, PostService, CarService
5. ru.job4j.cars.controller
   - HomeController (список объявлений), PostController (CRUD объявлений), AuthController (вход/регистрация пользователя)

## TODO-лист проекта

✅ Справочники: Mark, Model (заполнить заранее тестовыми данными)

✅ Сущности: User, Model, Mark, Car, Post (аннотации, связи)

✅ Репозитории: AbstractCrudRepository + конкретные методы (findById, findAll, findByUser, …

✅ Сервисы: UserService (регистрация, аутентификация), PostService (создать, найти, пометить «продано»)

✅ Контроллеры: HomeController, PostController, AuthController

✅ Thymeleaf:  
    – home.html (таблица/карточки объявлений)
    - post.html (конкретный пост на продажу)
    - login.html (логин)
    - register.html (регистрация)
    – add.html (форма добавления поста)  
    - edit.html (форма редактирования поста)
    – fragments/header.html (общая навигация)  
    – templates для работы с ошибками/404
    
✅ Хранение фото
    
✅ Аутентификация:  
    - Фильтры для незарегистрированных пользователей
    - Доступ к редактированию поста для создателя (user)
    - Доступ к добавлениям постов только для зарегистрированных пользователей
    
❌ Тесты:  
    – Модульные — `PostServiceTest`, `CarServiceTest`.  
    – Интеграционные, если остаётся время H2 + Hibernate.  


