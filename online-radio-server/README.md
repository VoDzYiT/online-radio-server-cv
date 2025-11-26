# Online Radio Station

Серверна частина системи онлайн-радіостанції

## Вимоги до оточення

Перед запуском переконайтеся, що встановлено:
1.  **Java 17** (JDK).
2.  **MySQL 8.0** (сервер бази даних).
3.  **Maven** (або використовуйте вбудований `mvnw`).

## Налаштування бази даних

1.  Запустіть MySQL Server.
2.  Створіть порожню базу даних з назвою `online_radio`.
    * SQL команда: `CREATE DATABASE online_radio;`
3.  Відкрийте файл конфігурації проєкту:
    `src/main/resources/application.properties`
4.  Перевірте налаштування підключення (змініть `username` та `password` на ваші):
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/online_radio
    spring.datasource.username=root
    spring.datasource.password=ваш_пароль
    ```

## Як запустити проєкт

1.  Відкрийте проєкт в IDEA.
2.  Дочекайтеся індексації Maven.
3.  Знайдіть клас `OnlineRadioServerApplication.java` і натисніть зелену кнопку **Run**.


## Як користуватися

Після успішного запуску відкрийте браузер:

* **Головна сторінка:** [http://localhost:8080](http://localhost:8080)
* **Адмін-панель:** [http://localhost:8080/admin](http://localhost:8080/admin)

### Дані для входу (створюються автоматично)

* **Адміністратор:**
    * Логін: `admin`
    * Пароль: `admin`
* **Користувач:**
    * Ви можете зареєструвати нового користувача через форму на сайті.
