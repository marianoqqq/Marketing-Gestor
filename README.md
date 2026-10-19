# Inmo Marketing API

Proyecto Spring Boot 4 · Java 21 · Maven · PostgreSQL · JWT.

## Setup local

1. Crear la base `inmo_marketing_db` en PostgreSQL.
2. Copiar `src/main/resources/application-local.properties.example` como
   `application-local.properties` y completar password de la DB y `jwt.secret`
   (`openssl rand -base64 64`). Ese archivo no se sube a git.

## Arrancar

```bash
./mvnw spring-boot:run
```

## Tests

```bash
./mvnw test
```

## Auth

| Método | Endpoint         | Descripción                         |
|--------|------------------|-------------------------------------|
| POST   | `/auth/register` | Registro (`email`, `password`, `role`) |
| POST   | `/auth`          | Login, devuelve access + refresh token |
| POST   | `/auth/refresh`  | Renueva el access token             |

El resto de endpoints requiere `Authorization: Bearer <accessToken>`.
