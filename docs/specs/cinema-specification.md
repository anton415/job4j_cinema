# Спецификация проекта `job4j_cinema`

## Источники

- Задание Job4j `2. Сервис - Кинотеатр [#504869]`.
- Требования к Web-проектам: `ShamRail/job4j_requirements/wiki`.
- Архитектурный ориентир: `anton415/job4j_dreamjob`.

## Цель проекта

Реализовать Web-приложение для одного кинотеатра. Приложение должно позволять любому пользователю смотреть главную страницу,
расписание сеансов и кинотеку, а зарегистрированному пользователю покупать билет на конкретный сеанс, ряд и место.

В рамках задания не реализуется административная панель. Данные о постерах, жанрах, фильмах, залах и сеансах заполняются SQL
скриптами. Пользователи и билеты создаются через интерфейс приложения.

## Технологический стек

- Java 21.
- Maven.
- Spring Boot 3.4.0.
- Spring MVC, Thymeleaf.
- Bootstrap, локальные CSS/JS файлы по аналогии с `job4j_dreamjob`.
- PostgreSQL для production-профиля и интеграционных тестов.
- Liquibase Maven plugin, SQL changelog в корневой директории `db`.
- Sql2o 1.6.0.
- Apache Commons DBCP2.
- JUnit 5, Spring Boot Test, Mockito.
- Checkstyle и JaCoCo.
- `net.jcip:jcip-annotations` для `@ThreadSafe`.

`pom.xml` должен быть минимальным и повторять подход `job4j_dreamjob`: parent `spring-boot-starter-parent`, профили
`production` и `test`, property `activeProfile`, property `liquibase.propertyFile`, плагины Spring Boot, Liquibase,
Checkstyle и JaCoCo.

## Структура репозитория

```text
.
├── checkstyle.xml
├── db
│   ├── dbchangelog.xml
│   ├── liquibase.properties
│   ├── liquibase_test.properties
│   └── scripts
├── docs
│   └── specs
├── files
├── img
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java
    │   │   └── ru/job4j/cinema
    │   │       ├── configuration
    │   │       ├── controller
    │   │       ├── dto
    │   │       ├── filter
    │   │       ├── model
    │   │       ├── repository
    │   │       └── service
    │   └── resources
    │       ├── application.properties
    │       ├── application-production.properties
    │       ├── application-test.properties
    │       ├── static
    │       │   ├── css
    │       │   └── js
    │       └── templates
    │           ├── errors
    │           ├── films
    │           ├── fragments
    │           ├── sessions
    │           ├── tickets
    │           └── users
    └── test
        ├── java
        │   └── ru/job4j/cinema
        └── resources
```

Файлы локального окружения, Maven wrapper, `target`, IDE-файлы и временные БД должны быть исключены через `.gitignore`.

## Конфигурация

- Корневой пакет: `ru.job4j.cinema`.
- Главный класс: `ru.job4j.cinema.Main`.
- Java-конфигурации лежат в `ru.job4j.cinema.configuration`.
- `DatasourceConfiguration` создает `BasicDataSource` и `Sql2o` bean.
- Для Sql2o нужен converter `Timestamp`/`LocalDateTime`, потому что `film_sessions.start_time` и `end_time` имеют тип
  `TIMESTAMP`.
- `application.properties`:

```properties
spring.profiles.active=@activeProfile@
file.directory=files
```

- `application-production.properties`:

```properties
datasource.url=jdbc:postgresql://127.0.0.1:5432/cinema
datasource.username=postgres
datasource.password=password
datasource.driver-class-name=org.postgresql.Driver
```

- `application-test.properties` должен указывать настройки отдельной тестовой PostgreSQL БД, например `cinema_test`.
- `db/liquibase_test.properties` должен использовать ту же тестовую PostgreSQL БД, чтобы интеграционные тесты проверяли
  SQL на реальном диалекте PostgreSQL.

## Миграции и данные

Liquibase выполняется через Maven plugin. Скрипты лежат в `db/scripts` и подключаются из `db/dbchangelog.xml` через
`sqlFile` с `relativeToChangelogFile="true"`.

Обязательные скрипты:

```text
001_ddl_create_files_table.sql
002_dml_insert_files.sql
003_ddl_create_genres_table.sql
004_dml_insert_genres.sql
005_ddl_create_films_table.sql
006_dml_insert_films.sql
007_ddl_create_halls_table.sql
008_dml_insert_halls.sql
009_ddl_create_film_sessions_table.sql
010_dml_insert_film_sessions.sql
011_ddl_create_users_table.sql
012_ddl_create_tickets_table.sql
```

Базовая схема должна соответствовать заданию:

```sql
CREATE TABLE files
(
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    path VARCHAR NOT NULL UNIQUE
);

CREATE TABLE genres
(
    id SERIAL PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL
);

CREATE TABLE films
(
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    "year" INT NOT NULL,
    genre_id INT REFERENCES genres (id) NOT NULL,
    minimal_age INT NOT NULL,
    duration_in_minutes INT NOT NULL,
    file_id INT REFERENCES files (id) NOT NULL
);

CREATE TABLE halls
(
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    row_count INT NOT NULL,
    place_count INT NOT NULL,
    description VARCHAR NOT NULL
);

CREATE TABLE film_sessions
(
    id SERIAL PRIMARY KEY,
    film_id INT REFERENCES films (id) NOT NULL,
    halls_id INT REFERENCES halls (id) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    price INT NOT NULL
);

CREATE TABLE users
(
    id SERIAL PRIMARY KEY,
    full_name VARCHAR NOT NULL,
    email VARCHAR UNIQUE NOT NULL,
    password VARCHAR NOT NULL
);

CREATE TABLE tickets
(
    id SERIAL PRIMARY KEY,
    session_id INT REFERENCES film_sessions (id) NOT NULL,
    row_number INT NOT NULL,
    place_number INT NOT NULL,
    user_id INT NOT NULL,
    UNIQUE (session_id, row_number, place_number)
);
```

Допустимое усиление схемы: добавить `REFERENCES users (id)` для `tickets.user_id`, сохранив имя колонки и остальные
ограничения. Если есть риск конфликта с автоматической проверкой, оставить DDL строго как в задании.

Постеры фильмов хранятся в директории `files`. В таблице `files.path` сохраняется относительный путь, например
`files/matrix.jpg`. Путь должен читаться через `FileService`, а не хардкодиться в шаблонах.

## Модели

Модели в пакете `ru.job4j.cinema.model` соответствуют таблицам БД:

- `File`: `id`, `name`, `path`.
- `Genre`: `id`, `name`.
- `Film`: `id`, `name`, `description`, `year`, `genreId`, `minimalAge`, `durationInMinutes`, `fileId`.
- `Hall`: `id`, `name`, `rowCount`, `placeCount`, `description`.
- `FilmSession`: `id`, `filmId`, `hallId`, `startTime`, `endTime`, `price`.
- `User`: `id`, `fullName`, `email`, `password`.
- `Ticket`: `id`, `sessionId`, `rowNumber`, `placeNumber`, `userId`.

Если имя колонки отличается от имени Java-поля, репозиторий должен делать явный alias в SQL, например
`halls_id AS hallId`.

## DTO

DTO лежат в `ru.job4j.cinema.dto`. Они нужны для представлений, потому что модели БД не содержат связанных данных:

- `FileDto`: `name`, `content`.
- `FilmDto`: `id`, `name`, `description`, `year`, `minimalAge`, `durationInMinutes`, `genre`, `fileId`.
- `FilmSessionDto`: `id`, `filmId`, `filmName`, `genre`, `hallName`, `startTime`, `endTime`, `price`, `fileId`.
- `TicketDto`: `id`, `sessionId`, `filmName`, `hallName`, `startTime`, `rowNumber`, `placeNumber`, `price`,
  `userFullName`.

Сборка DTO выполняется в сервисном слое. Контроллеры не должны самостоятельно обращаться к нескольким репозиториям.

## Репозитории

Каждый репозиторий имеет интерфейс и Sql2o-реализацию в пакете `ru.job4j.cinema.repository`.

| Интерфейс | Реализация | Обязательные методы |
| --- | --- | --- |
| `FileRepository` | `Sql2oFileRepository` | `Optional<File> findById(int id)` |
| `GenreRepository` | `Sql2oGenreRepository` | `List<Genre> findAll()`, `Optional<Genre> findById(int id)` |
| `FilmRepository` | `Sql2oFilmRepository` | `List<Film> findAll()`, `Optional<Film> findById(int id)` |
| `HallRepository` | `Sql2oHallRepository` | `Optional<Hall> findById(int id)` |
| `FilmSessionRepository` | `Sql2oFilmSessionRepository` | `List<FilmSession> findAll()`, `Optional<FilmSession> findById(int id)` |
| `UserRepository` | `Sql2oUserRepository` | `Optional<User> save(User user)`, `Optional<User> findByEmailAndPassword(String email, String password)` |
| `TicketRepository` | `Sql2oTicketRepository` | `Optional<Ticket> save(Ticket ticket)`, `Optional<Ticket> findById(int id)`, `List<Ticket> findBySessionId(int sessionId)` |

`Sql2oUserRepository.save` и `Sql2oTicketRepository.save` должны ловить нарушение уникальности PostgreSQL `SQLState 23505`
и возвращать `Optional.empty()`. Остальные ошибки пробрасываются выше.

## Сервисы

Сервисы имеют интерфейс и `Simple...Service` реализацию в пакете `ru.job4j.cinema.service`. Реализации зависят от
интерфейсов репозиториев, а не от конкретных классов.

| Интерфейс | Реализация | Ответственность |
| --- | --- | --- |
| `FileService` | `SimpleFileService` | Чтение постера по `fileId`, возврат `FileDto` |
| `FilmService` | `SimpleFilmService` | Получение фильмов и сборка `FilmDto` через `FilmRepository` и `GenreRepository` |
| `FilmSessionService` | `SimpleFilmSessionService` | Получение расписания, сборка `FilmSessionDto`, получение рядов и мест по залу |
| `UserService` | `SimpleUserService` | Регистрация и вход пользователя |
| `TicketService` | `SimpleTicketService` | Покупка билета, проверка сеанса, зала и пользователя, возврат результата покупки |

`TicketService.buy(Ticket ticket)` возвращает `Optional<Ticket>` или `Optional<TicketDto>`. `Optional.empty()` означает,
что место уже занято или билет не удалось сохранить из-за уникального ограничения.

Количество рядов и мест берется из `halls.row_count` и `halls.place_count`, а не хардкодится.

## Web-слой

Контроллеры лежат в `ru.job4j.cinema.controller`, имеют суффикс `Controller` и не содержат бизнес-логики.

| URL | Метод | Контроллер | Шаблон/результат | Доступ |
| --- | --- | --- | --- | --- |
| `/`, `/index` | GET | `IndexController` | `index` | Все |
| `/films` | GET | `FilmController` | `films/list` | Все |
| `/sessions` | GET | `FilmSessionController` | `sessions/list` | Все |
| `/tickets/buy/{sessionId}` | GET | `TicketController` | `tickets/buy` | Все |
| `/tickets` | POST | `TicketController` | redirect на success/failure/login | Только авторизованные |
| `/tickets/success/{ticketId}` | GET | `TicketController` | `tickets/success` | Авторизованные |
| `/tickets/failure/{sessionId}` | GET | `TicketController` | `tickets/failure` | Все |
| `/files/{id}` | GET | `FileController` | bytes response | Все |
| `/users/register` | GET | `UserController` | `users/register` | Все |
| `/users/register` | POST | `UserController` | redirect `/` или форма с ошибкой | Все |
| `/login` | GET | `UserController` | `users/login` | Все |
| `/login` | POST | `UserController` | redirect `/` или форма с ошибкой | Все |
| `/logout` | GET | `UserController` | redirect `/` | Авторизованные |

Страница покупки билета показывает:

- название фильма;
- описание фильма;
- жанр;
- возрастное ограничение;
- длительность;
- зал;
- время начала и окончания;
- цену;
- выпадающий список рядов;
- выпадающий список мест;
- кнопки `Купить` и `Отменить`.

Если гость отправляет покупку, приложение перенаправляет его на `/login`. После успешной покупки показывается страница
`tickets/success`. Если место уже купили, показывается `tickets/failure` с понятным сообщением и ссылкой назад на страницу
покупки билета для этого сеанса.

## Шаблоны

Шаблоны реализуются на Thymeleaf и Bootstrap. Общие части выносятся в `templates/fragments`:

- `header.html`;
- `navigation.html`;
- `footer.html`.

Навигационная панель:

- логотип ведет на `/`;
- `Расписание` ведет на `/sessions`;
- `Кинотека` ведет на `/films`;
- для гостя показываются `Регистрация` и `Вход`;
- для авторизованного пользователя показываются имя пользователя и `Выйти`.

Шаблоны ошибок лежат в `templates/errors` и содержат текст ошибки и ссылку для продолжения сценария.

## Фильтры и сессия

Повторить подход `job4j_dreamjob`:

- `SessionUser` хранит имя session attribute `user`, умеет возвращать гостевого пользователя и проверять авторизацию.
- `SessionFilter` добавляет текущего пользователя в request attribute `user`.
- `AuthorizationFilter` пропускает публичные страницы и статические ресурсы, а защищенные действия перенаправляет на
  `/login`.

Публичные URI и префиксы:

- `/`, `/index`, `/films`, `/sessions`, `/tickets/buy/`, `/tickets/failure/`;
- `/login`, `/users/register`, `/files/`, `/error`, `/favicon.ico`;
- `/css/`, `/js/`, `/images/`, `/webjars/`.

Защищенные действия:

- `POST /tickets`;
- `GET /tickets/success/{ticketId}`;
- `GET /logout`.

## README

`README.md` должен соответствовать требованиям `job4j_requirements`:

- название `job4j_cinema`;
- описание проекта своими словами;
- стек с версиями;
- требования к окружению;
- команды запуска, включая создание БД `cinema` и запуск Liquibase;
- описание взаимодействия с приложением;
- скриншоты страниц в директории `img`;
- контакты.

## Тестирование

Обязательные тесты:

- repository integration tests на PostgreSQL с Liquibase:
  - `Sql2oUserRepositoryTest`: успешная регистрация, дублирование email возвращает `Optional.empty()`;
  - `Sql2oTicketRepositoryTest`: успешная покупка, повторное место на том же сеансе возвращает `Optional.empty()`;
  - `Sql2oFilmRepositoryTest`, `Sql2oFilmSessionRepositoryTest`: чтение заполненных SQL-данных;
- service unit tests с Mockito:
  - `SimpleFilmServiceTest`: сборка `FilmDto` с жанром;
  - `SimpleFilmSessionServiceTest`: сборка расписания и списков рядов/мест;
  - `SimpleTicketServiceTest`: успешная покупка, занятое место, несуществующий сеанс;
  - `SimpleFileServiceTest`: чтение существующего и отсутствующего файла;
- controller tests через Spring MVC:
  - `IndexControllerTest`;
  - `FilmControllerTest`;
  - `FilmSessionControllerTest`;
  - `TicketControllerTest`;
  - `UserControllerTest`;
- filter tests:
  - публичные страницы доступны гостю;
  - `POST /tickets` гостя перенаправляет на `/login`;
  - авторизованный пользователь проходит к защищенным действиям.

Тестовые методы называются по шаблону `when...Then...` и содержат JavaDoc с описанием сценария. Минимальное покрытие
JaCoCo: 50%.

## План реализации

1. Создать Maven-проект, `.gitignore`, `checkstyle.xml`, профили, зависимости и плагины.
2. Добавить конфигурации приложения и подключения к БД.
3. Создать Liquibase changelog, DDL/DML скрипты и директорию `files` с постерами.
4. Создать модели, DTO и FileService/FileController.
5. Реализовать репозитории на Sql2o и интеграционные тесты.
6. Реализовать сервисы и модульные тесты.
7. Реализовать контроллеры, фильтры, SessionUser и MVC/filter тесты.
8. Создать Thymeleaf-шаблоны и Bootstrap-навигацию.
9. Заполнить README со скриншотами.
10. Проверить `mvn test`, `mvn checkstyle:check`, `mvn jacoco:report`, запуск приложения и основные UI-сценарии.

## Готово, если

- Проект запускается локально на production-профиле с PostgreSQL и БД `cinema`.
- Liquibase создает схему и заполняет справочные таблицы.
- Любой пользователь видит главную страницу, расписание, кинотеку и форму покупки.
- Гость при попытке купить билет перенаправляется на страницу входа.
- Пользователь может зарегистрироваться, войти, купить свободное место и увидеть успешный результат.
- Повторная покупка того же места на том же сеансе не создает билет и показывает страницу неудачной покупки.
- Навигационная панель меняется в зависимости от авторизации.
- Постеры фильмов отдаются через `/files/{id}`.
- Все тесты проходят, Checkstyle не падает, JaCoCo показывает покрытие не ниже 50%.
- README заполнен по требованиям Web-проектов Job4j.
