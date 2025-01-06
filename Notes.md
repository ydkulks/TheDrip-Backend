# TheDrip - ザ・ドリップ
**Lowkey Anime, High-Key Fashion**

## TODO
- [x] REST
- [ ] CRUD
- [ ] Authorization
- [ ] Authentication

## ⚠️ Pre-requizits:
- Java
- Spring boot
- Maven/Gradle

```sh
# Install SDKMan and required SDKs
curl -s "https://get.sdkman.io" | bash
sdk install java
sdk install springboot
sdk install gradle
```

## ⚙️ Initialize spring boot project
There are multiple methods to initialize the spring boot project:

1. [Spring Initializer (recommended)](https://start.spring.io)
2. Spring CLI

    ```sh
    spring init <project-name>
    ```

> [!Note] Dependencies
> Web, JPA, DevTools

## 🏃🏻 Run Project

1. List out all the available tasks of gradle
```sh
gradle tasks
```

2. Run project using gradle wrapper

For this project, if you are running the following commands for the first
time; it will download specific gradle binary version to build the project.
This can be commited to version controle.

```sh
./gradlew bootRun # if you have gradlew or gradlew.bat file at root of the project
./gradlew build # for production
```

> [!Warning] Timeout Issue
> If you are not able to download the gradle binary when you run the project
> for the first time, increase the timeout time in `gradle-wrapper.properties`

## MVC Project Architecture
```txt
com.domain.ProjectName/
    Controller(API)             : Handle HTTP requests
    Model                       : Convert JSON to Object and vise versa
    Service(Repository/Query)   : DBMS data manipulation
    Bean                        : Structure?
```

## 🌐 Useful links
- [Spring Initializer](https://start.spring.io)
- [REST API tutorial](https://www.springboottutorial.com/spring-boot-crud-rest-service-with-jpa-hibernate)
- [Annotations](https://www.geeksforgeeks.org/top-spring-boot-annotations/)
- [Architectural patterns](https://dev.to/chiragagg5k/architecture-patterns-for-beginners-mvc-mvp-and-mvvm-2pe7?ref=dailydev)
- [MySQL connection](https://www.geeksforgeeks.org/how-to-work-with-databases-using-spring-boot/)
- [H2 connection](https://spring.io/guides/gs/accessing-data-jpa)
- [JPA Entities](https://www.baeldung.com/jpa-entities)
- [Nonsense](https://nonsense.jp/)
