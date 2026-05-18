# План реализации `job4j_cinema`

План основан на [спецификации проекта](cinema-specification.md). Реализация идет по слоям, как в `job4j_dreamjob`:
сначала инфраструктура и БД, затем модели, репозитории, сервисы, Web-слой, тесты и README.

## Общие правила

- Не использовать H2. Интеграционные тесты репозиториев выполнять на отдельной PostgreSQL БД `cinema_test`.
- Каждый слой закрывать тестами до перехода к следующему слою.
- Не переносить бизнес-логику в контроллеры: контроллеры только принимают запросы, вызывают сервисы и выбирают view.
- DTO для представлений собирать в сервисах.
- Все SQL-изменения выполнять через Liquibase.
- Сохранять разумную историю коммитов: один логический шаг - один коммит.

## Этап 1. Базовая структура проекта

### Задачи

- [x] Создать `pom.xml` с parent `spring-boot-starter-parent` версии `3.4.0`.
- [x] Добавить зависимости:
  - `spring-boot-starter-web`;
  - `spring-boot-starter-thymeleaf`;
  - `spring-boot-devtools`;
  - `postgresql`;
  - `commons-dbcp2`;
  - `sql2o`;
  - `jcip-annotations`;
  - `spring-boot-starter-test`;
  - `mockito-core`, если не хватает транзитивных зависимостей Spring Boot Test.
- [x] Настроить Maven-профили `production` и `test`.
- [x] Подключить плагины:
  - `spring-boot-maven-plugin`;
  - `liquibase-maven-plugin`;
  - `maven-checkstyle-plugin`;
  - `jacoco-maven-plugin`.
- [x] Добавить `checkstyle.xml`.
- [x] Добавить `.gitignore`.
- [x] Создать корневой класс `ru.job4j.cinema.Main`.
- [x] Создать пакеты:
  - `configuration`;
  - `controller`;
  - `dto`;
  - `filter`;
  - `model`;
  - `repository`;
  - `service`.
- [x] Создать директории `db/scripts`, `files`, `img`, `templates`, `static/css`, `static/js`.

### Проверка

```bash
mvn -q -DskipTests compile
mvn checkstyle:check
```

### Коммит

```text
Настроен Maven-проект cinema
```

## Этап 2. Конфигурация PostgreSQL и Liquibase

### Задачи

- [ ] Создать production БД:

```sql
CREATE DATABASE cinema;
```

- [ ] Создать тестовую БД:

```sql
CREATE DATABASE cinema_test;
```

- [ ] Добавить `src/main/resources/application.properties`.
- [ ] Добавить `src/main/resources/application-production.properties` для БД `cinema`.
- [ ] Добавить `src/main/resources/application-test.properties` для БД `cinema_test`.
- [ ] Добавить `db/liquibase.properties` для БД `cinema`.
- [ ] Добавить `db/liquibase_test.properties` для БД `cinema_test`.
- [ ] Создать `DatasourceConfiguration`:
  - `BasicDataSource`;
  - `Sql2o`;
  - converter для `LocalDateTime`.
- [ ] Создать `db/dbchangelog.xml`.
- [ ] Добавить DDL/DML скрипты из спецификации:
  - `files`;
  - `genres`;
  - `films`;
  - `halls`;
  - `film_sessions`;
  - `users`;
  - `tickets`.
- [ ] Положить постеры в `files` и указать относительные пути в `002_dml_insert_files.sql`.

### Проверка

```bash
mvn -Pproduction liquibase:update
mvn -Ptest liquibase:update
```

При необходимости пересоздать тестовую схему:

```bash
mvn -Ptest liquibase:dropAll liquibase:update
```

### Коммит

```text
Добавлены миграции БД кинотеатра
```

## Этап 3. Модели и DTO

### Задачи

- [ ] Создать модели:
  - `File`;
  - `Genre`;
  - `Film`;
  - `Hall`;
  - `FilmSession`;
  - `User`;
  - `Ticket`.
- [ ] Для моделей добавить конструкторы, getters/setters, `equals` и `hashCode`.
- [ ] Создать DTO:
  - `FileDto`;
  - `FilmDto`;
  - `FilmSessionDto`;
  - `TicketDto`.
- [ ] Убедиться, что Java-поля соответствуют SQL alias в будущих запросах:
  - `genre_id AS genreId`;
  - `file_id AS fileId`;
  - `halls_id AS hallId`;
  - `start_time AS startTime`;
  - `end_time AS endTime`;
  - `row_number AS rowNumber`;
  - `place_number AS placeNumber`;
  - `user_id AS userId`.

### Проверка

```bash
mvn -q -DskipTests compile
mvn checkstyle:check
```

### Коммит

```text
Созданы модели и DTO кинотеатра
```

## Этап 4. Репозитории и интеграционные тесты

### Задачи

- [ ] Создать интерфейсы репозиториев:
  - `FileRepository`;
  - `GenreRepository`;
  - `FilmRepository`;
  - `HallRepository`;
  - `FilmSessionRepository`;
  - `UserRepository`;
  - `TicketRepository`.
- [ ] Создать реализации:
  - `Sql2oFileRepository`;
  - `Sql2oGenreRepository`;
  - `Sql2oFilmRepository`;
  - `Sql2oHallRepository`;
  - `Sql2oFilmSessionRepository`;
  - `Sql2oUserRepository`;
  - `Sql2oTicketRepository`.
- [ ] В `Sql2oUserRepository.save` обрабатывать уникальность `email` через `Optional.empty()`.
- [ ] В `Sql2oTicketRepository.save` обрабатывать уникальность `(session_id, row_number, place_number)` через
  `Optional.empty()`.
- [ ] Для PostgreSQL проверять `SQLState 23505`.
- [ ] Создать `Sql2oTestHelper` для очистки таблиц в правильном порядке перед тестами.
- [ ] Написать интеграционные тесты на PostgreSQL:
  - `Sql2oUserRepositoryTest`;
  - `Sql2oTicketRepositoryTest`;
  - `Sql2oFilmRepositoryTest`;
  - `Sql2oFilmSessionRepositoryTest`;
  - при необходимости `Sql2oFileRepositoryTest`, `Sql2oGenreRepositoryTest`, `Sql2oHallRepositoryTest`.

### Проверка

```bash
mvn -Ptest test -Dtest=*RepositoryTest
```

### Коммит

```text
Реализованы Sql2o репозитории cinema
```

## Этап 5. Сервисы и unit-тесты

### Задачи

- [ ] Создать интерфейсы:
  - `FileService`;
  - `FilmService`;
  - `FilmSessionService`;
  - `UserService`;
  - `TicketService`.
- [ ] Создать реализации:
  - `SimpleFileService`;
  - `SimpleFilmService`;
  - `SimpleFilmSessionService`;
  - `SimpleUserService`;
  - `SimpleTicketService`.
- [ ] В `SimpleFilmService` собрать `FilmDto` из `films`, `genres`, `files`.
- [ ] В `SimpleFilmSessionService` собрать расписание с фильмом, жанром, залом, временем и ценой.
- [ ] В `SimpleFilmSessionService` формировать списки рядов и мест по данным `Hall`, без хардкода.
- [ ] В `SimpleTicketService` проверить существование сеанса, зала и пользователя перед покупкой.
- [ ] В `SimpleTicketService` вернуть `Optional.empty()`, если место занято.
- [ ] Написать unit-тесты с Mockito:
  - `SimpleFileServiceTest`;
  - `SimpleFilmServiceTest`;
  - `SimpleFilmSessionServiceTest`;
  - `SimpleUserServiceTest`;
  - `SimpleTicketServiceTest`.

### Проверка

```bash
mvn test -Dtest=*ServiceTest
```

### Коммит

```text
Реализованы сервисы cinema
```

## Этап 6. Контроллеры, фильтры и сессия

### Задачи

- [ ] Создать `SessionUser` по аналогии с `job4j_dreamjob`.
- [ ] Создать `SessionFilter`.
- [ ] Создать `AuthorizationFilter`.
- [ ] Публичными оставить:
  - `/`;
  - `/index`;
  - `/films`;
  - `/sessions`;
  - `/tickets/buy/`;
  - `/tickets/failure/`;
  - `/login`;
  - `/users/register`;
  - `/files/`;
  - `/error`;
  - статические ресурсы.
- [ ] Защитить:
  - `POST /tickets`;
  - `/tickets/success/`;
  - `/logout`.
- [ ] Создать контроллеры:
  - `IndexController`;
  - `FilmController`;
  - `FilmSessionController`;
  - `TicketController`;
  - `UserController`;
  - `FileController`.
- [ ] Реализовать сценарии:
  - просмотр главной страницы;
  - просмотр кинотеки;
  - просмотр расписания;
  - открытие формы покупки;
  - редирект гостя на `/login` при покупке;
  - успешная покупка;
  - неуспешная покупка занятого места;
  - регистрация;
  - вход;
  - выход.
- [ ] Написать MVC/filter тесты:
  - `IndexControllerTest`;
  - `FilmControllerTest`;
  - `FilmSessionControllerTest`;
  - `TicketControllerTest`;
  - `UserControllerTest`;
  - `FileControllerTest`;
  - `AuthorizationFilterTest`;
  - `SessionFilterTest`.

### Проверка

```bash
mvn test -Dtest=*ControllerTest,*FilterTest
```

### Коммит

```text
Реализован Web-слой cinema
```

## Этап 7. Thymeleaf-шаблоны и Bootstrap

### Задачи

- [ ] Добавить локальные Bootstrap CSS/JS файлы.
- [ ] Создать fragments:
  - `fragments/header.html`;
  - `fragments/navigation.html`;
  - `fragments/footer.html`.
- [ ] Создать страницы:
  - `index.html`;
  - `films/list.html`;
  - `sessions/list.html`;
  - `tickets/buy.html`;
  - `tickets/success.html`;
  - `tickets/failure.html`;
  - `users/register.html`;
  - `users/login.html`;
  - `errors/404.html`;
  - при необходимости `errors/400.html`.
- [ ] В навигации показывать `Регистрация` и `Вход` для гостя.
- [ ] В навигации показывать имя пользователя и `Выйти` для авторизованного пользователя.
- [ ] На странице покупки вывести два `select`: ряд и место.
- [ ] Постеры выводить через `/files/{id}`.

### Проверка

```bash
mvn spring-boot:run
```

Проверить вручную в браузере:

- `/`;
- `/films`;
- `/sessions`;
- `/tickets/buy/{sessionId}`;
- регистрация;
- вход;
- покупка билета;
- повторная покупка занятого места.

### Коммит

```text
Добавлены страницы cinema
```

## Этап 8. README и визуальная проверка

### Задачи

- [ ] Заполнить `README.md`:
  - название проекта;
  - описание;
  - стек с версиями;
  - требования к окружению;
  - запуск проекта;
  - взаимодействие с приложением;
  - контакты.
- [ ] Сделать скриншоты страниц и положить их в `img`.
- [ ] Добавить ссылки на скриншоты в README.
- [ ] Проверить, что в репозитории нет IDE-файлов, `target`, временных БД и локальных артефактов.

### Проверка

```bash
git status --short
mvn test
mvn checkstyle:check
mvn jacoco:report
```

### Коммит

```text
Оформлен README проекта cinema
```

## Финальная проверка

- [ ] Production БД `cinema` создается и мигрируется через Liquibase.
- [ ] Тестовая БД `cinema_test` создается и мигрируется через Liquibase.
- [ ] `mvn test` проходит.
- [ ] `mvn checkstyle:check` проходит.
- [ ] `mvn jacoco:report` показывает покрытие не ниже 50%.
- [ ] Приложение запускается через `mvn spring-boot:run`.
- [ ] Гость видит главную, кинотеку, расписание и форму покупки.
- [ ] Гость при покупке попадает на `/login`.
- [ ] Пользователь регистрируется и входит.
- [ ] Пользователь покупает свободное место.
- [ ] Повторная покупка того же места показывает страницу неудачи.
- [ ] Постеры фильмов открываются через `/files/{id}`.
- [ ] README содержит скриншоты и команды запуска.

## Рекомендуемый порядок коммитов

1. `Настроен Maven-проект cinema`
2. `Добавлены миграции БД кинотеатра`
3. `Созданы модели и DTO кинотеатра`
4. `Реализованы Sql2o репозитории cinema`
5. `Реализованы сервисы cinema`
6. `Реализован Web-слой cinema`
7. `Добавлены страницы cinema`
8. `Оформлен README проекта cinema`
