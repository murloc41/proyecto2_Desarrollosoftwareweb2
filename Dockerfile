# Etapa de build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Instalar utilidades necesarias
RUN apk add --no-cache maven

COPY pom.xml .
COPY src ./src

# Construir el JAR (sin tests para rapidez en CI/CD; ajustar si se requiere)
RUN mvn -B -DskipTests clean package

# Etapa de runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Crear usuario no root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el jar desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto (usar PORT env en PaaS)
EXPOSE 8080

# Variables de entorno por defecto
ENV JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
