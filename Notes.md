# 🎩 TheDrip - ザ・ドリップ
**Lowkey Anime, High-Key Fashion**

## ✅ TODO
- [x] Basic REST
- [X] Basic CRUD
- [x] Authentication
- [x] Authorization
- [x] Role based auth for API endpoints
- [ ] Products
    - [ ] Upload images
        - [x] Base64
        - [ ] [S3](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html)
            - [x] Upload multiple images
            - [x] Get presigned link for one image
            - [x] Get presigned link for more than one image
            - [x] Update images
            - [ ] Delete images
    - [ ] Upload product details
    - [x] Link images on S3 to its associated product
    - [ ] Get product details
    - [ ] Product ratings
    - [ ] Searching
    - [ ] Sorting
    - [ ] Pagination
- [ ] Cart
- [ ] Order
- [ ] Feedback form

## ⚠️ Pre-requizits:
1. **Java**
2. **Spring boot**
3. **Maven/Gradle**

```sh
# Install SDKMan and required SDKs
curl -s "https://get.sdkman.io" | bash
sdk install java
sdk install springboot
sdk install gradle
```

## ⚙️ Initialize spring boot project
There are multiple methods to initialize the spring boot project:

1. **[Spring Initializer (recommended)](https://start.spring.io)**
2. **Spring CLI**

    ```sh
    spring init <project-name>
    ```

> [!Note]
> Dependencies: Web, JPA, DevTools, { Database }, Lombok

## 🏃🏻 Run Project

1. **List out all the available tasks of gradle**

    ```sh
    gradle tasks
    ```

2. **Run project using gradle wrapper**

    ```sh
    ./gradlew bootRun # if you have gradlew or gradlew.bat file at root of the project
    ./gradlew build # for production
    ```

> [!Note]
> For this project, if you are running the following commands for the first
> time; it will download specific gradle binary version to build the project.
> This can be commited to version controle.

> [!Tip]
> If you are not able to download the gradle binary when you run the project
> for the first time, increase the timeout time in `gradle-wrapper.properties`

## 📐 MVC Project Architecture
`com.domain.ProjectName/`

- **Controller** : Handle HTTP requests
- **Service**    : Business logic
- **Model**      : Entities corresponding to database tables
- **Repository** : Database interactions using JPA

## ⛃ Database setup(PostgreSQL)
0. **Update package repository database**

    ```sh
    sudo pacman -Syy
    ```
1. **Install PostgreSQL**

    ```sh
    sudo pacman -S postgresql
    ```
2. **Initialize database directory**

    ```sh
    sudo -iu postgres # Login as 'postgres' user
    initdb --locale $LANG -E UTF8 -D '/var/lib/postgres/data/'
    exit
    ```
3. **Start PostgreSQL server**

    ```sh
    su # Login as root user
    sudo systemctl start postgresql
    sudo systemctl status postgresql
    sudo systemctl enable postgresql
    exit
    ```
4. **GUI for PostgreSQL**

    ```sh
    yay -S pgadmin4-desktop
    ```

Open the app and create server and database before moving on with the next step.

5. **Create a user to access a database**

    ```sh
    psql -U postgres
    ```
    ```sql
    \du
    CREATE ROLE user_name WITH LOGIN PASSWORD 'your_password';
    GRANT ALL PRIVILEGES ON DATABASE database_name TO user_name;
    ```
6. **Uninstall PostgreSQL**

    ```sh
    sudo pacman -Rcns postgresql
    ```

## 🔌 API Endpoints
### Products
- **`GET /products/{id}`**: Retrieve specific product details by its ID.
- **`GET /products`**: Retrieve a list of all products with their details (e.g., pagination, sorting).
- **`GET /products/search`**: Search for products based on keywords, categories, brands, etc.
- **`GET /products/categories`**: Retrieve a list of product categories with their subcategories and products.
- **`GET /products/brands`**: Retrieve a list of product brands with their associated products.
- **`GET /products/tags`**: Retrieve a list of products tagged with specific keywords or tags.
- **`GET /products/images`**: Retrieve a list of product images (e.g., thumbnails, high-resolution).
- **`GET /products/reviews`**: Retrieve a list of product reviews and ratings.
- **`GET /products?filter=[key]=value`**: Apply filters to products (e.g., price range, brand).
- **`GET /products?sort=[field]&order=[asc/desc]`**: Sort products based on a specific field in ascending or descending order.
- **`GET /products?page={number}&limit={size}`**: Retrieve paginated list of products with specified page number and limit size.

## 🌐 Useful links
- [Spring Initializer](https://start.spring.io)
- [REST API tutorial](https://www.springboottutorial.com/spring-boot-crud-rest-service-with-jpa-hibernate)
- [Annotations](https://www.geeksforgeeks.org/top-spring-boot-annotations/)
- [Architectural patterns](https://dev.to/chiragagg5k/architecture-patterns-for-beginners-mvc-mvp-and-mvvm-2pe7?ref=dailydev)
- [MVN Repository](https://mvnrepository.com)
- [Spring MVC](https://www.marcobehler.com/guides/spring-mvc)
- [MySQL connection](https://www.geeksforgeeks.org/how-to-work-with-databases-using-spring-boot/)
- [H2 connection](https://spring.io/guides/gs/accessing-data-jpa)
- [JPA Entities](https://www.baeldung.com/jpa-entities)
- [AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html)
- [Nonsense](https://nonsense.jp/)
