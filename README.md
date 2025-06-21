# Pet Project

<details>
<summary><b>Table of contents</b></summary>
  <ol>
    <li><a href="#-description">Description</a></li>
    <li><a href="#-technologies">Technologies</a></li>
    <li><a href="#-demo">Demo</a></li>
    <li>
      <a href="#-getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#usage">Usage</a></li>
      </ul>
    </li>
    <li><a href="#-useful-links">Useful Links</a></li>
    <li><a href="#sequences-diagrams">Sequences diagrams</a></li>
    <li><a href="#-author">Author</a></li>
    <li><a href="#-license">License</a></li>
  </ol>
</details>

### 📖 Description

This project is a comprehensive toolbox application featuring robust CRUD operations and seamless integration with modern technologies such as Spring Boot, Kafka, and Docker. Designed for scalability and maintainability, it leverages best practices in software engineering, including automated testing and CI/CD workflows via GitHub Actions. Ideal for learning, prototyping, or as a foundation for other service.

### 📦 Technologies

[![Spring Boot][springboot-shield]][springboot-url] [![Java][java-shield]][java-url] [![Maven][maven-shield]][maven-url] [![Kafka][kafka-shield]][kafka-url] [![Docker][docker-shield]][docker-url]

<details>
    <summary>Maven dependencies</summary>

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
    <summary>Maven configuration</summary>

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

### 🚀 Demo

You can check the [Pet Project here](demo-url)

![home-page](./images/home-page.png)
The `index.html` page serves as a simple dashboard for the project, providing quick access links to Swagger UI, H2 Console, Kafka UI, JaCoCo coverage report, and the GitHub repository. It helps users easily navigate the main tools and resources of the application.


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
- Check if the containers, kafka and kafbat-ui, are up
    ![docker-ps](./images/docker-ps.png)
    _OBS: Kafbat is one user interface for Apache Kafka_

- Build the project:
    ```sh
    mvn clean package
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


### 🐱‍👤 Author

**Ambrósia Andrade** :brazil: <br/>
[![GitHub][github-shield]][github-url] [![LinkedIn][linkedin-shield]][linkedin-url] [![Gmail][gmail-shield]][gmail-url] [![Instagram][instagram-shield]][instagram-url]

### 📝 License

<!--todo--> 
_TODO: Create and update the license_

This project is under the license **X**.

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

[docker-shield]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[docker-url]: https://www.docker.com/

[instagram-shield]: https://img.shields.io/badge/-Instagram-E4405F?style=for-the-badge&logo=instagram&logoColor=white
[instagram-url]:https://www.instagram.com/ambrosia_andrade_br/

[js-shield]: https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=black
[js-url]: https://www.javascript.com
[ts-shield]: https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=TypeScript&logoColor=white
[ts-url]: https://www.typescriptlang.org
[angular-shield]: https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white
[angular-url]: https://angular.io/
[bootstrap.com]: https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white
[bootstrap-url]: https://getbootstrap.com
[jquery.com]: https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jquery&logoColor=white
[jquery-url]: https://jquery.com
[vscode-shield]: https://img.shields.io/badge/-Visual%20Studio%20Code-007ACC?style=for-the-badge&logo=Visual%20Studio%20Code
[vscode-url]: https://code.visualstudio.com
[node-shield]: https://img.shields.io/badge/-Node.js-339933?style=for-the-badge&logo=Node.js&logoColor=white
[node-url]: https://nodejs.org/en/
[open-jdk-shield]: https://img.shields.io/badge/-OpenJDK-000?style=for-the-badge&logo=OpenJDK&logoColor=white
[open-jdk-url]: https://openjdk.org
[python-shield]: https://img.shields.io/badge/-Python-3776AB?style=for-the-badge&logo=Python&logoColor=white
[python-url]: https://www.python.org
[auth0-shield]: https://img.shields.io/badge/-Auth0-EB5424?style=for-the-badge&logo=Python&logoColor=white
[auth0-url]: https://auth0.com
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=blue
[linkedin-url]: https://linkedin.com/in/ambrosiaandrade
[gmail-shield]: https://img.shields.io/badge/-Gmail-EA4335?style=for-the-badge&logo=gmail&logoColor=white
[gmail-url]: mailto:ambrosiaandrade.pe@gmail.com
[github-shield]: https://img.shields.io/badge/-GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white
[github-url]: https://github.com/ambrosiaandrade
