# Inmo Marketing API 🏡✨

> **API REST para inmobiliarias:** Gestión de propiedades, fotografías y generación automatizada de contenido publicitario para redes sociales con Inteligencia Artificial.

---

## 🚀 Tecnologías

- **Lenguaje:** Java 21
- **Framework:** Spring Boot 4 (`4.0.6`)
- **Seguridad:** Spring Security con JWT (JSON Web Tokens) Stateless
- **Base de Datos:** PostgreSQL en la nube (**Neon**) / Soporte H2 para tests
- **ORM:** Spring Data JPA / Hibernate
- **Inteligencia Artificial:**
  * **Spring AI** (`2.0.0-RC1`)
  * **Groq** (`openai/gpt-oss-120b` / `llama-3.3-70b-versatile`) para redacción ultrarrápida de posts y sugerencia de ganchos publicitarios
  * Preparado para **OpenAI** (`GPT-Image-2.5` / `DALL-E`) para generación de flyers e imágenes
- **Herramienta de Construcción:** Maven Wrapper (`./mvnw`)

---

## ⚙️ Configuración y Setup Local

### 1. Clonar el repositorio
```bash
git clone https://github.com/marianoqqq/Marketing-Gestor.git
cd Marketing-Gestor
```

### 2. Configurar `application-local.properties`
Copiá el archivo de ejemplo o creá directamente:
`src/main/resources/application-local.properties` *(este archivo debe estar en `.gitignore` y no subirse al repositorio)*.

Pegá el siguiente contenido reemplazando con tus credenciales:

```properties
# ==============================================================
# JWT Secret para entorno local (clave en Base64 de al menos 256 bits)
# ==============================================================
jwt.secret=tu_jwt_secret_en_base64_aqui

# ==============================================================
# Groq (Generación de texto y copys con IA)
# ==============================================================
spring.ai.openai.api-key=gsk_tu_api_key_de_groq
spring.ai.openai.chat.options.model=openai/gpt-oss-120b

# ==============================================================
# OpenAI (Generación de imágenes con GPT-Image-2.5 / DALL-E)
# ==============================================================
# openai.image.api-key=sk-...
# openai.image.model=gpt-image-2.5
# openai.image.base-url=https://api.openai.com/v1

# ==============================================================
# Base de Datos: Neon PostgreSQL (Nube)
# ==============================================================
spring.datasource.url=jdbc:postgresql://tu-host-de-neon.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password_aqui
spring.datasource.driver-class-name=org.postgresql.Driver
```

> **Nota:** Hibernate tiene activo `ddl-auto=update`, por lo que las tablas se sincronizan automáticamente con la base de datos al iniciar la aplicación.

---

## ▶️ Ejecución y Tests

### Arrancar la aplicación
```bash
./mvnw.cmd spring-boot:run
# En Linux/Mac:
./mvnw spring-boot:run
```
La API quedará escuchando en `http://localhost:8080`.

### Correr los tests
```bash
./mvnw.cmd test
```

---

## 📡 Endpoints de la API

Todos los endpoints (excepto `/auth/**`) requieren la cabecera:
`Authorization: Bearer <accessToken>`

### 🔐 1. Autenticación (`/auth`)
| Método | Endpoint | Descripción | Body / Parámetros |
|---|---|---|---|
| `POST` | `/auth/register` | Registro de usuario (crea a su vez su Inmobiliaria) | `nombre`, `email`, `password`, `nombreInmobiliaria` |
| `POST` | `/auth` | Inicio de sesión, devuelve `accessToken` y `refreshToken` | `email`, `password` |
| `POST` | `/auth/refresh` | Renueva el `accessToken` usando el refresh token | `refreshToken` |

---

### 🏢 2. Inmobiliaria (`/api/inmobiliaria`)
| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/inmobiliaria` | Obtiene el perfil de la inmobiliaria del usuario autenticado |
| `PUT` | `/api/inmobiliaria` | Actualiza datos (nombre, teléfono, Instagram, dirección, logoUrl) |

---

### 🏠 3. Propiedades (`/api/propiedades`)
| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/propiedades` | Crea una propiedad vinculada a la inmobiliaria |
| `GET` | `/api/propiedades` | Lista todas las propiedades de la inmobiliaria |
| `GET` | `/api/propiedades/{id}` | Obtiene el detalle de una propiedad |
| `PUT` | `/api/propiedades/{id}` | Modifica una propiedad existente |
| `DELETE` | `/api/propiedades/{id}` | Elimina una propiedad |

---

### 📸 4. Archivos y Fotos (`/api`)
| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/propiedades/{id}/fotos` | Sube fotos multipart (`JPG`, `PNG`, `WEBP`) para una propiedad |
| `GET` | `/api/propiedades/{id}/fotos` | Lista las fotos subidas de una propiedad |
| `GET` | `/api/archivos/{id}` | Descarga o visualiza el archivo binario |

---

### 🤖 5. Publicaciones con IA (`/api/publicaciones`)
Flujo para la creación y publicación de contenido para redes sociales:

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/publicaciones` | Crea una publicación en estado `BORRADOR` (`propiedadId`, `red`: `INSTAGRAM` / `FACEBOOK`, `formato`: `IMAGEN`, `indicaciones`, `estiloImagen`) |
| `GET` | `/api/publicaciones` | Lista todas las publicaciones de la inmobiliaria |
| `GET` | `/api/publicaciones/{id}` | Detalle de una publicación |
| `DELETE` | `/api/publicaciones/{id}` | Elimina una publicación |
| `POST` | `/api/publicaciones/{id}/sugerir-texto-imagen` | **IA (Groq):** Genera una frase o gancho publicitario corto de alto impacto para estampar en la imagen |
| `PATCH` | `/api/publicaciones/{id}/texto-imagen` | Permite al usuario editar el texto o estilo de la imagen antes de publicar |
| `POST` | `/api/publicaciones/{id}/generar` | **IA (Groq):** Genera el copy del post con hashtags, emojis y contexto visual. Pasa a estado `LISTA` |
| `POST` | `/api/publicaciones/{id}/publicada` | Marca la publicación como `PUBLICADA` registrando la fecha y hora actual |

#### Ciclo de vida de una Publicación:
```text
[ BORRADOR ] ──(Generar con IA)──> [ LISTA ] ──(Usuario publica)──> [ PUBLICADA ]
      │                                 ▲
      └──(Error de IA)──> [ ERROR ] ────┘
```

---

### 👥 6. Usuarios Admin (`/api/users`)
*Exclusivo para usuarios con rol `ROLE_ADMIN`.*
- `POST /api/users` - Crear usuario
- `GET /api/users` - Listar usuarios
- `GET /api/users/{id}` - Ver usuario
- `PUT /api/users/{id}` - Modificar usuario
- `DELETE /api/users/{id}` - Eliminar usuario
