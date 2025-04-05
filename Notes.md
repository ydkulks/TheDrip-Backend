# 🎩 TheDrip - ザ・ドリップ
**Lowkey Anime, High-Key Fashion**

## ✅ TODO
- [x] Basic REST
- [X] Basic CRUD
- [x] Authentication
- [x] Authorization
- [x] Role based auth for API endpoints
- [ ] User profile
    - [x] Verify if user exists before user creation
    - [ ] Get user data
    - [ ] Update user data
    - [ ] Password reset
    - [ ] Delete user data
    - [x] User reviews
        - [x] Create
        - [x] Get
        - [x] Update
        - [x] Delete
    - [x] Cart
        - [x] Create
        - [x] Get
        - [x] Update
        - [x] Delete
        - [x] Different cart entry for product variants
    - [x] Checkout
        - [x] Stripe integration
        - [x] Discount (Promo Code)
        - [x] Shipping rate
        - [x] Tax rate
        - [x] Checkout multiple products at once
- [x] Products
    - [x] Upload images
        - [x] Store S3 meta-data in DB
        - [x] [S3](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html)
            - [x] Upload multiple images
            - [x] Get presigned link for one image
            - [x] Get presigned link for more than one image
            - [x] Fetch presigned link only if existing link is expired
            - [x] Update images
            - [x] Delete images
        - [x] Limit images per product
        - [x] Filter to fetch specified number of images with product details
        - [x] Bulk upload
    - [x] Upload product details
    - [x] Update product details
    - [x] Link images on S3 to its associated product
    - [x] Get product details
    - [x] Searching by name
    - [x] Filter by categories, seller, series
    - [x] Sorting by name, price, stock
    - [x] Pagination
    - [x] Product sold
    - [x] Delete product
    - [ ] Add product rating
    - [ ] Product Discount
- [ ] Feedback form
- [ ] Jaspersoft report

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
> This can be committed to version control.

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
### User
1. **Signup**: Create user account with 3 types of roles - `Admin`, `Seller`, `Customer`

    ```sh
    curl --location 'http://localhost:8080/api/signup' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "email":"admin@gmail.com",
        "username":"admin",
        "password":"admin@123",
        "role":"Admin"
    }'
    ```

2. **Login**

    ```sh
    curl --location 'http://localhost:8080/api/login' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "username": "admin",
        "password": "admin@123"
    }'
    ```

3. **Products**
    - **Create products**: Create new products under a user id.

        ```sh
        curl --location 'http://localhost:8080/seller/products' \
        --header 'Content-Type: application/json' \
        --header 'Authorization: Bearer {TOKEN}' \
        --data '[{
          "productName": "Edgerunner Blue Cargo Sweatpants",
          "categoryId": 7,
          "userId": 2,
          "seriesId": 1,
          "productPrice": 45.00,
          "productDescription": "Printed logo on the pocket. Drawstring waist, Side pocket. Zip back pocket",
          "productStock": 100,
          "productSold": 0,
          "productSizes": [
            1, 3, 5
          ],
          "productColors": [1, 2, 3]
        }]'
        ```

    - **Update products**: Update existing products.

        ```sh
        curl --location 'http://localhost:8080/seller/products?productId=7' \
        --header 'Content-Type: application/json' \
        --header 'Authorization: Bearer {TOKEN}' \
        --data '[{
          "productName": "Edgerunner Blue Cargo Sweatpants",
          "categoryId": 7,
          "userId": 2,
          "seriesId": 1,
          "productPrice": 45.00,
          "productDescription": "Printed logo on the pocket. Drawstring waist, Side pocket. Zip back pocket",
          "productStock": 100,
          "productSold": 0,
          "productSizes": [
            1, 3, 5
          ],
          "productColors": [1, 2, 3]
        }]'
        ```

    - **Upload or Update product images**: Upload product images to S3 bucket.

        ```sh
        curl --location 'http://localhost:8080/seller/{username}/{product_id}/image' \
        --header 'Authorization: Bearer {TOKEN}' \
        --form 'file=@"{path/to/image/file.jpg}"' \
        --form 'file=@"{path/to/image/file.jpg}"'
        ```

    - **Get pre-signed URL of images**: Get a limited access link to view images.

        ```sh
        # Get one image
        curl --location 'http://localhost:8080/seller/{username}/{product_id}/{filename}' \
        --header 'Authorization: Bearer {TOKEN}'
        # Get all the images from one product
        curl --location 'http://localhost:8080/seller/{username}/{product_id}/image' \
        --header 'Authorization: Bearer {TOKEN}'
        # Get all the images from all the products of one user
        curl --location 'http://localhost:8080/seller/{username}/product/image' \
        --header 'Authorization: Bearer {TOKEN}'
        # Get all the images from all the products of all the users
        curl --location 'http://localhost:8080/seller/all/product/image' \
        --header 'Authorization: Bearer {TOKEN}'
        ```

    - **Delete product images**: Delete product images to S3 bucket.

        ```sh
        # Delete one image from one product
        curl --location --request DELETE 'http://localhost:8080/seller/{username}/{product_id}/{filename}' \
        --header 'Authorization: Bearer {TOKEN}'
        # Delete all the images from one product
        curl --location --request DELETE 'http://localhost:8080/seller/{username}/{product_id}/image' \
        --header 'Authorization: Bearer {TOKEN}'
        # Delete all the images from  all the products of one user
        curl --location --request DELETE 'http://localhost:8080/seller/{username}/product/image' \
        --header 'Authorization: Bearer {TOKEN}'
        # Delete all the images from  all the products of all the user
        curl --location --request DELETE 'http://localhost:8080/seller/all/product/image' \
        --header 'Authorization: Bearer {TOKEN}'
        ```

    - **Get product by ID**: Retrieve specific product details by its ID.

        ```sh
        curl --location 'http://localhost:8080/api/product?id=1'
        ```

    - **Get products by IDs**: Retrieve specific products by its IDs (more than one) for updating. It returns IDs as values instead of names to make things easier for updating (no images).

        ```sh
        curl --location 'http://localhost:8080/api/productsallbyid?id=14,15'
        ```

    - **Get all products**: Retrieve a list of all products with their details (e.g., pagination, sorting).

        ```sh
        curl --location 'http://localhost:8080/api/products?page=0&size=10'
        ```

    - **Search products**: Search for products based on title and description.

        ```sh
        curl --location 'http://localhost:8080/api/products?searchTerm=sweatpants&page=0&size=10'
        ```

    - **Filter products**: Apply filters to products (e.g., price range, brand).

        ```sh
        curl --location 'http://localhost:8080/api/products?userId=2&categoryIds=5,10&seriesIds=1,2&colorIds=2,3&sizeIds=4,5&isStock=true&minPrice=10&maxPrice=100&page=0&size=10'
        ```

    - **Sort products**: Sort products based on a specific field in ascending or descending order.

        ```sh
        curl --location 'http://localhost:8080/api/products?sortDirection=asc&sortBy=productPrice&page=0&size=10'
        ```

    - **Delete product**: Delete a product from the database.

        ```sh
        curl --location 'http://localhost:8080/seller/product?productId=4'
        ```

    - **Create or Update Series**: Create new or update existing series

        ```sh
        curl --location --request POST 'http://localhost:8080/seller/series?seriesName=Dragon%20Ball%20Super%3A%20Super%20Hero' \
        --header 'Authorization: Bearer {TOKEN}'
        ```

    - **Reviews**: Leave a review for a product

        ```sh
        # Create or update the review
        curl --location 'http://localhost:8080/customer/review' \
        --header 'Content-Type: application/json' \
        --header 'Authorization: Bearer {TOKEN}' \
        --data '{
            "user":3,
            "product":7,
            "review_title":"test review title",
            "review_text":"This is a test review for this cargo sweatpants.",
            "rating": 4
        }'
        # Get paginated reviews with filters and sort
        curl --location 'http://localhost:8080/api/reviews?userId=2&productId=7&sortBy=user&sortDirection=asc&page=0&size=10'
        ```

    - **Cart**: Manage products that you are interested to buy in cart.

        ```sh
        # Add to cart or Update the cart
        curl --location --request POST 'http://localhost:8080/customer/items?userId=2&productId=5&color=1&size=1&quantity=1' \
        --header 'Authorization: Bearer {TOKEN}'
        # Get cart items
        curl --location 'http://localhost:8080/customer/items?userId=2&productName=cargo&colorId=1&sizeId=2&sortBy=cartItemsId&sortDirection=desc&page=0&size=10' \
        --header 'Authorization: Bearer {TOKEN}'
        # Delete by ID
        curl --location --request DELETE 'http://localhost:8080/customer/item?cartItemId=2' \
        --header 'Authorization: Bearer {TOKEN}'
        ```

    - **Checkout**: Make payment for the products.

        ```sh
        curl --location 'http://localhost:8080/api/stripe/create-checkout-session' \
        --header 'Content-Type: application/json' \
        --header 'Authorization: Bearer {TOKEN}' \
        --data '{
            "successUrl": "http://localhost:5173",
            "cancelUrl": "http://localhost:5173",
            "products": [
                {
                    "productId": 1,
                    "qty": 2
                },
                {
                    "productId": 13,
                    "qty": 1
                }
            ]
        }'
        ```

## 🧪 TDD
> [!Tip]
> Testing private methods in unit testing can break encapsulation as these methods
> are meant to be accessed only within the class they belong to. Instead of
> exposing these methods for testing, it's better to test the observable behavior through
> public methods. Private methods impact the internal state or public method
> outputs, which should be tested instead of the private methods themselves.
> [🔗](https://dev.to/canro91/this-is-why-we-dont-test-private-methods-28ef?ref=dailydev)

## 💰 Stripe
Stripe is a payment gateway to handle payments.

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
- [Stripe Docs](https://docs.stripe.com)
- [Stripe Dashboard](https://dashboard.stripe.com)
- [Stripe SDK Docs](https://stripe.dev/stripe-java/com/stripe)
