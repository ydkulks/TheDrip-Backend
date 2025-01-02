# CaféCraft
Ecommerce website using ReactJS for front-end and Spring boot for back-end

## Pre-requizits:
- Java
- Spring boot
- Gradle

```sh
# Install SDKMan and required SDKs
curl -s "https://get.sdkman.io" | bash
sdk install java
sdk install springboot
sdk install gradle
```

## Initialize spring boot project
There are multiple methods to initialize the spring boot project:
1. [Spring Initializer (recommended)](https://start.spring.io)
2. Spring CLI
```sh
spring init <project-name>
```

## Run back-end
```sh
gradle tasks # List tasks
# For this project, if you are running the following commands for the first 
# time; it will download specific gradle binary version to build the project.
# This can be commited to version controle.
./gradlew bootRun # if you have gradlew or gradlew.bat file at root of the project
./gradlew build # for production
## NOTE: Timeout Issue
## Increase the timeout time in gradle-wrapper.properties
```
