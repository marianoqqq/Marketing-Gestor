# ========================================================
# Etapa 1: Compilación y empaquetado (Build Stage)
# ========================================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copiar descriptor del proyecto para cachear dependencias
COPY pom.xml .

# Descargar dependencias para aprovechar el cache de capas de Docker
RUN mvn dependency:go-offline -B || true

# Copiar el código fuente del proyecto
COPY src ./src

# Compilar y empaquetar omitiendo tests unitarios
RUN mvn clean package -DskipTests -B

# ========================================================
# Etapa 2: Imagen de ejecución en producción (Runtime Stage)
# ========================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear directorio de trabajo para subidas de archivos
RUN mkdir -p uploads/fotos

# Copiar el artefacto JAR generado desde la etapa previa
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto por defecto de la aplicación
EXPOSE 8080

# Comando de inicio del contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]
