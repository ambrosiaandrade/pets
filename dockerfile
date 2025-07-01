# Etapa 1: build (multi-stage build para evitar imagem pesada)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia o código e o arquivo pom.xml
COPY . .

# Faz o build do projeto com Maven
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Etapa 2: imagem final com apenas o jar
FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
WORKDIR /app

# Copia o JAR da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando de entrada
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
