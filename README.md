# Pet Project
A modern Spring Boot RESTful API project following MVC architecture, featuring Kafka integration, Docker support, automated testing, and CI/CD with GitHub Actions.

<details>
<summary><b>Table of contents</b></summary>
  <ol>
    <li><a href="#-description">Description</a></li>
    <li><a href="#-features">Features</a></li>
    <li><a href="#-technologies">Technologies</a>
      <ul><li><a href="#maven">Maven</a></li></ul>
    </li>
    <li>
      <a href="#-getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#usage">Usage</a></li>
      </ul>
    </li>
    <li><a href="#-demo">Demo</a></li>
    <li><a href="#-folder-structure">Folder Structure</a></li>
    <li><a href="#-useful-links">Useful Links</a></li>
    <li><a href="#-sequence-diagrams">Sequence Diagrams</a></li>
    <li><a href="#-testing">Testing</a></li>
    <li><a href="#-ci-and-cd">CI and CD</a></li>
    <li><a href="#-contributing">Contributing</a></li>
    <li><a href="#-author">Author</a></li>
    <li><a href="#-license">License</a></li>
  </ol>
</details>

### 📖 Description

This project is a comprehensive toolbox application featuring robust CRUD operations and seamless integration with modern technologies such as Spring Boot, Kafka, and Docker. Designed for scalability and maintainability, it leverages best practices in software engineering, including automated testing and CI/CD workflows via GitHub Actions. Ideal for learning, prototyping, or serving as a foundation for other services.

### ✨ Features

* **🔁 Full CRUD Operations** – Manage entities through RESTful APIs with clear separation of concerns.
* **📄 Pagination Support** – Efficiently handle large datasets with pageable endpoints.
* **⚙️ Asynchronous Endpoints** – Demonstrates how to run non-blocking operations using `CompletableFuture`.
* **📨 Kafka Integration** – Includes Kafka producers and consumers with example topics and message flow.
* **📊 Sequence Diagrams** – Visual documentation of key controller interactions using PlantUML.
* **🧪 Test Coverage with JaCoCo** – Enforces 80% code coverage and generates readable HTML reports.
* **🧰 Swagger Integration** – Auto-generated API documentation using SpringDoc OpenAPI.
* **📋 H2 In-Memory Database** – Lightweight database for development and testing purposes.
* **📦 Docker-Ready** – Includes a `docker-compose` setup for Kafka and UI tools.
* **📈 Actuator Monitoring** – Spring Boot Actuator enabled for health checks and system monitoring.
* **♻️ DevTools Reloading** – Supports hot reloading via Spring DevTools for a smoother development experience.
* **🧹 Clean Architecture Principles** – Follows a layered structure with clear separation between controllers, services, models, and mappers, making the codebase easier to maintain, test, and extend.
* **🔐 Modular Dependency Setup** – Maven configuration using annotation processors and profile-based builds.
* **📁 Static Dashboard** – `index.html` provides shortcuts to useful development tools and visual aids.


### 📦 Technologies

[![Spring Boot][springboot-shield]][springboot-url] [![Spring Data JPA][jpa-shield]][jpa-url] [![Spring Web][web-shield]][web-url] [![Spring Boot Test][test-shield]][test-url] [![Springdoc OpenAPI][springdoc-shield]][springdoc-url] [![Spring Actuator][actuator-shield]][actuator-url] [![Spring Cache][cache-shield]][cache-url] [![DevTools][devtools-shield]][devtools-url]

[![Java][java-shield]][java-url] [![Maven][maven-shield]][maven-url] [![Kafka][kafka-shield]][kafka-url] [![Kafka UI][kafbat-shield]][kafbat-url] [![Docker][docker-shield]][docker-url] [![JUnit 5][junit5-shield]][junit5-url] [![Mockito][mockito-shield]][mockito-url] [![Swagger UI][swagger-shield]][swagger-url] [![H2 Database][h2-shield]][h2-url] [![Lombok][lombok-shield]][lombok-url] [![MapStruct][mapstruct-shield]][mapstruct-url]

##### Maven

<details>
    <summary>Dependencies</summary>

```xml
<properties>
    <java.version>21</java.version>
    <lombok.version>1.18.30</lombok.version>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
    <jacoco.version>0.8.10</jacoco.version>
    <springdoc.version>2.8.8</springdoc.version>
    <spring.boot.maven.plugin>3.2.5</spring.boot.maven.plugin>
    <maven.resources.plugin.version>3.3.1</maven.resources.plugin.version>
</properties>
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>${springdoc.version}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
</dependencies>
```
</details>

<details>
    <summary>Configuration</summary>

- _Spring Boot plugin to build executable JARs_
- _Maven compiler plugin with annotation processors for Lombok and MapStruct_
- _Loads dependency properties (can be removed if not used)_
- _Runs unit tests and sets up Mockito Java agent (if needed)_
- _JaCoCo plugin to measure code coverage, minimum 80%_
- _Copies the generated JaCoCo report to the static resources folder for frontend access_

```xml
<build>
    <plugins>
        <!-- Spring Boot plugin to build executable JARs -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <version>${spring.boot.maven.plugin}</version>
            <executions>
                <execution>
                    <goals>
                        <goal>repackage</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>

        <!-- Maven compiler plugin with annotation processors for Lombok and MapStruct -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>${java.version}</source>
                <target>${java.version}</target>
                <generatedSourcesDirectory>${project.build.directory}/generated-sources/annotations
                </generatedSourcesDirectory>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${mapstruct.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>

        <!-- Loads dependency properties (can be removed if not used) -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <executions>
                <execution>
                    <goals>
                        <goal>properties</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>

        <!-- Runs unit tests and sets up Mockito Java agent (if needed) -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <configuration>
                <argLine>
                    -javaagent:${settings.localRepository}/org/mockito/mockito-core/${mockito.version}/mockito-core-${mockito.version}.jar
                </argLine>
            </configuration>
        </plugin>

        <!-- JaCoCo plugin to measure code coverage -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>${jacoco.version}</version>
            <executions>
                <!-- Prepares the JaCoCo agent before running tests -->
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                    <configuration>
                        <excludes>
                            <exclude>org/jcp/xml/**</exclude>
                            <exclude>com/sun/**</exclude>
                            <exclude>sun/*</exclude>
                            <exclude>java/*</exclude>
                            <exclude>jdk/*</exclude>
                            <exclude>javax/*</exclude>
                            <exclude>**/*Application.class</exclude>
                            <exclude>**/*$HibernateInstantiator.class</exclude>
                            <exclude>**/*$Proxy*.class</exclude>
                            <exclude>**/*$HibernateProxy*.class</exclude>
                            <exclude>**/*$EnhancerBySpringCGLIB*.class</exclude>
                        </excludes>
                    </configuration>
                </execution>

                <!-- Generates HTML report after running tests -->
                <execution>
                    <id>report</id>
                    <phase>verify</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                    <configuration>
                        <excludes>
                            <exclude>**/models/**</exclude>
                            <exclude>**/entities/**</exclude>
                            <exclude>**/enums/**</exclude>
                            <exclude>**/exceptions/**</exclude>
                            <exclude>**/interfaces/**</exclude>
                            <exclude>**/*Application.class</exclude>
                        </excludes>
                    </configuration>
                </execution>

                <!-- Enforces a minimum coverage threshold during build -->
                <execution>
                    <id>check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>BUNDLE</element>
                                <limits>
                                    <limit>
                                        <counter>INSTRUCTION</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.80</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                        <excludes>
                            <exclude>**/models/**</exclude>
                            <exclude>**/entities/**</exclude>
                            <exclude>**/enums/**</exclude>
                            <exclude>**/exceptions/**</exclude>
                            <exclude>**/interfaces/**</exclude>
                            <exclude>**/*Application.class</exclude>
                        </excludes>
                    </configuration>
                </execution>
            </executions>
        </plugin>

        <!-- Copies the generated JaCoCo report to the static resources folder for frontend access -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-resources-plugin</artifactId>
            <version>${maven.resources.plugin.version}</version>
            <executions>
                <execution>
                    <id>copy-jacoco-report</id>
                    <phase>prepare-package</phase>
                    <goals>
                        <goal>copy-resources</goal>
                    </goals>
                    <configuration>
                        <outputDirectory>${project.basedir}/src/main/resources/static/jacoco</outputDirectory>
                        <resources>
                            <resource>
                                <directory>${project.build.directory}/site/jacoco</directory>
                                <filtering>false</filtering>
                            </resource>
                        </resources>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
</details>

### 💡 Getting Started

##### Prerequisites

You should have the following installed in your machine:

- Java 21+
- Maven
- Docker & Docker Compose

##### Usage

- Start the services with docker before running Spring Boot Application, at project's root folder run the command:
    ```sh
    docker-compose up --build
    ```
    ![docker-compose-up](./images/docker-compose-up.png)
- Ensure that the containers (Kafka and Kafbat UI) are running
    ![docker-ps](./images/docker-ps.png)
    _OBS: Note: Kafbat is a web-based UI for managing Apache Kafka topics and messages._

- Build the project:
    ```sh
    mvn clean package
    ```

### 🚀 Demo

You can check the [Pet Project here](demo-url)

![home-page](./images/home-page.png)
The `index.html` serves as a lightweight dashboard offering quick access to Swagger, H2 Console, Kafka UI, JaCoCo reports, and the GitHub repo.

### 🏗️ Folder Structure
```bash
src/
├── main/
│   ├── java/
│   │   └── com/ambrosiaandrade/pets/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── model/
│   │       └── ...
│   └── resources/
│       └── static/
└── test/
```

### 🔗 Useful Links

- [Swagger UI](http://localhost:8080/swagger-ui/index.html)
    ![swagger-ui](./images/swagger-ui.png) 

- [H2 Console](http://localhost:8080/h2-console/)
    ![h2-console](./images/h2-console.png) 

- [Kafka UI](http://localhost:8081)
    ![kafbat-ui](./images/kafbat-ui.png)  

### 🗂️ Sequences diagrams

Editable .puml files can be found at `./docs` folder.

<details>
<summary><b>Animal Controller</b></summary>

  ![sequence-diagram-animal-controller](./docs/sequence-diagram-animal-controller.png)

</details>

<details>
<summary><b>Pagination Controller</b></summary>

  ![sequence-diagram-pagination-controller](./docs/sequence-diagram-pagination-controller.png)

</details>

<details>
<summary><b>Async Controller</b></summary>

This controller serves as a demonstration of asynchronous processing in Java. It is intended for educational purposes and does not perform any real business logic.

  ![sequence-diagram-async-controller](./docs/sequence-diagram-async-controller.png)

</details>

<details>
<summary><b>Kafka Controller</b></summary>

  ![sequence-diagram-kafka-controller](./docs/sequence-diagram-kafka-controller.png)

</details>

### 🧪 Testing

This project uses JUnit and Spring Boot Test for unit testing, along with Mockito for mocking dependencies.  
Code coverage is enforced with JaCoCo (80% minimum threshold), and the report is generated under `/static/jacoco/index.html`.

To run tests:
```sh
mvn test
```

### ⚙️ CI and CD

This project uses **GitHub Actions** for Continuous Integration and Continuous Deployment.  
Every push and pull request triggers the workflow to:

- Build the project
- Run all tests and enforce code coverage
- Build and (optionally) push Docker images
- (Optionally) Deploy to your server or cloud

You can find the workflow definition in [`.github/workflows/`](.github/workflows/) in this repository.

![GitHub Actions Status](https://github.com/ambrosiaandrade/pets/actions/workflows/maven-tests.yml/badge.svg)

### 🤝 Contributing

Contributions are welcome! If you'd like to help improve this project, feel free to fork it and submit a pull request.

### 🐱‍👤 Author

**Ambrósia Andrade** :brazil: <br/>
[![GitHub][github-shield]][github-url] [![LinkedIn][linkedin-shield]][linkedin-url] [![Gmail][gmail-shield]][gmail-url] [![Instagram][instagram-shield]][instagram-url]

### 📝 License

This project is licensed under the **[MIT License](LICENSE)** — see the file for details.

<!-- MARKDOWN LINKS & IMAGES -->

[demo-url]: http://localhost:8080/

[springboot-shield]: https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[springboot-url]: https://spring.io/projects/spring-boot

[java-shield]: https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=java&logoColor=white
[java-url]: https://www.oracle.com/java/

[maven-shield]: https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white
[maven-url]: https://maven.apache.org/

[kafka-shield]: https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white
[kafka-url]: https://kafka.apache.org/

[kafbat-shield]: https://img.shields.io/badge/Kafbat_UI-4B286D?style=for-the-badge&logo=apachekafka&logoColor=white
[kafbat-url]: https://github.com/kafbat/kafbat

[docker-shield]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[docker-url]: https://www.docker.com/

[junit5-shield]: https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white
[junit5-url]: https://junit.org/junit5/

[mockito-shield]: https://img.shields.io/badge/Mockito-4EA94B?style=for-the-badge&logo=mockito&logoColor=white
[mockito-url]: https://site.mockito.org/

[swagger-shield]: https://img.shields.io/badge/Swagger_UI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black
[swagger-url]: http://localhost:8080/swagger-ui/index.html

[h2-shield]: https://img.shields.io/badge/H2-4479A1?style=for-the-badge&logo=h2&logoColor=white
[h2-url]: https://www.h2database.com/

[jpa-shield]: https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[jpa-url]: https://spring.io/projects/spring-data-jpa

[web-shield]: https://img.shields.io/badge/Spring%20Web-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[web-url]: https://spring.io/projects/spring-boot

[lombok-shield]: https://img.shields.io/badge/Lombok-18A303?style=for-the-badge&logo=lombok&logoColor=white
[lombok-url]: https://projectlombok.org/

[test-shield]: https://img.shields.io/badge/Spring%20Boot%20Test-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[test-url]: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing

[mapstruct-shield]: https://img.shields.io/badge/MapStruct-6C3483?style=for-the-badge&logo=mapbox&logoColor=white
[mapstruct-url]: https://mapstruct.org/

[springdoc-shield]: https://img.shields.io/badge/Springdoc%20OpenAPI-49A942?style=for-the-badge&logo=openapiinitiative&logoColor=white
[springdoc-url]: https://springdoc.org/

[actuator-shield]: https://img.shields.io/badge/Spring%20Actuator-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[actuator-url]: https://docs.spring.io/spring-boot/docs/current/actuator-api/htmlsingle/

[cache-shield]: https://img.shields.io/badge/Spring%20Cache-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[cache-url]: https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io.caching

[devtools-shield]: https://img.shields.io/badge/DevTools-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[devtools-url]: https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools

<!-- Social media -->

[instagram-shield]: https://img.shields.io/badge/-Instagram-E4405F?style=for-the-badge&logo=instagram&logoColor=white
[instagram-url]:https://www.instagram.com/ambrosia_andrade_br/

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=blue
[linkedin-url]: https://linkedin.com/in/ambrosiaandrade

[gmail-shield]: https://img.shields.io/badge/-Gmail-EA4335?style=for-the-badge&logo=gmail&logoColor=white
[gmail-url]: mailto:ambrosiaandrade.pe@gmail.com

[github-shield]: https://img.shields.io/badge/-GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white
[github-url]: https://github.com/ambrosiaandrade