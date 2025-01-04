# TheDrip
Ecommerce website using ReactJS for front-end and Spring boot for back-end

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

## 🌐 Useful links
- [Spring Initializer](https://start.spring.io)
- [REST API tutorial](https://www.springboottutorial.com/spring-boot-crud-rest-service-with-jpa-hibernate)
- [Annotations](https://www.geeksforgeeks.org/top-spring-boot-annotations/)
- [Architectural patterns](https://dev.to/chiragagg5k/architecture-patterns-for-beginners-mvc-mvp-and-mvvm-2pe7?ref=dailydev)
- [MySQL connection](https://www.geeksforgeeks.org/how-to-work-with-databases-using-spring-boot/)
- [H2 connection](https://spring.io/guides/gs/accessing-data-jpa)
- [Nonsense](https://nonsense.jp/)

## Company's possible description
1. **"Trendy Clothes for the Lowkey Anime Fan"**

Welcome to The Drip, your go-to spot for fashionably fresh clothes that are secretly inspired by anime. We're not trying to be too
on-the-nose (you know, for those who don't want to scream "I'm an anime fan!") but still want to nod to the shows and characters
you love. Our clothes are designed to make you look fly without drawing attention away from your favorite shows.

2. **"Drip Culture Meets Anime Obsession"**

The Drip is where fashion meets fandom. We're all about creating clothes that are trendy, comfortable, and – let's be real – subtly
inspired by anime. Whether you're a casual fan or a die-hard Otaku, we've got you covered with our stylish street wear collection.

3. **"Stay on Trend, Not on the Surface"**

At The Drip, we know that true style is about more than just following trends. It's about owning your aesthetic and expressing
yourself through fashion. That's why our clothes are designed to be subtle yet stylish – nodding to anime and manga without being
too obvious (or trying too hard). Come explore our collection and level up your wardrobe.

4. **"The Drip: Where Fashion Meets Anime Confidential"**

Want to look fire without screaming "I'm an anime fan"? The Drip is your spot. Our clothes are designed to be fashionably on-point
while subtly incorporating elements of anime culture that only true fans will appreciate. It's our way of saying, "Hey, I see you."

5. **"Unapologetically Anime-Inspired Fashion"**

We're not afraid to show some love for the shows and characters we adore. At The Drip, our fashion is inspired by anime, but it's
not a costume. It's style. It's attitude. And it's available to anyone who wants to rep their fandom without being too extra.

6. **"Lowkey Anime, High-Key Fashion"**

The Drip is where you can find trendy clothes that are secretly infused with anime magic. We're not trying to be loud or obnoxious;
we just want to create fashion that's both stylish and relatable to the anime fan in all of us.

7. **"Fashion That Speaks Volumes (But Not Too Loudly)"**

Our clothes are designed to make a statement, but only if you know where to look. At The Drip, we're passionate about creating
fashion that nods to anime culture without being too on-the-nose. It's our way of saying, "We see you, and we appreciate your
fandom."

8. **"The Ultimate Fashion Destination for Anime Lovers"**

Welcome to The Drip, where fashion meets anime obsession. We're all about creating clothes that are both stylish and subtly
inspired by the shows and characters you love. Whether you're a casual fan or an avid collector, we've got something for everyone.

These descriptions aim to capture the essence of "The Drip" as a fashion store that caters to fans who appreciate subtle
anime-inspired street wear. The tone is playful, confident, and inclusive, with a dash of GenZ slang to connect with the target
audience.
