# Inmo Marketing API 🏡✨

> **API REST para inmobiliarias:** Gestión de propiedades, fotografías y generación automatizada de contenido publicitario para redes sociales con Inteligencia Artificial.

---

## 📋 Requisitos funcionales (MVP)

- RF-01. El sistema debe permitir registrarse con nombre, email, contraseña y nombre de la inmobiliaria. Al registrarse se crea la inmobiliaria y el usuario queda asociado a ella con rol usuario.
- RF-02. El sistema no debe permitir registrar un email que ya existe.
- RF-03. El sistema debe exigir una contraseña de al menos 8 caracteres.
- RF-04. El sistema debe permitir iniciar sesión con email y contraseña y devolver un token de acceso y un token de renovación.
- RF-05. El sistema debe permitir renovar el token de acceso usando el token de renovación, sin volver a pedir la contraseña.
- RF-06. El sistema debe rechazar con error 401 los pedidos sin token, con token vencido o con token inválido.
- RF-07. El sistema no debe dejar iniciar sesión a un usuario bloqueado o inactivo.
- RF-08. Cada usuario solo puede ver y modificar los datos de su propia inmobiliaria.
- RF-09. El sistema debe permitir ver los datos de la propia inmobiliaria: nombre, teléfono, Instagram, dirección y logo.
- RF-10. El sistema debe permitir editar esos datos. El nombre es obligatorio (hasta 50 caracteres), el teléfono hasta 30, el Instagram hasta 15 y la dirección hasta 255.
- RF-11. Los datos de la inmobiliaria se muestran en el panel. Por ahora la IA no los usa: los textos e imágenes se arman solo con los datos de la propiedad y de la publicación.
- RF-12. El sistema debe permitir cargar una propiedad con título, tipo (casa, departamento, PH, terreno o local), operación (venta o alquiler), estado, precio, dirección, ambientes, metros cuadrados y descripción.
- RF-13. Son obligatorios el título (hasta 150 caracteres), el tipo, la operación y el precio. El precio y los metros cuadrados deben ser mayores a 0, y los ambientes al menos 1.
- RF-14. El sistema debe permitir listar las propiedades de la inmobiliaria y ver el detalle de cada una.
- RF-15. El sistema debe permitir editar una propiedad y cambiar su estado entre disponible, reservada y vendida.
- RF-16. El sistema debe permitir borrar una propiedad.
- RF-17. El sistema debe permitir subir una o varias fotos a una propiedad. Solo acepta JPG, PNG o WEBP, de hasta 5 MB cada una y 25 MB por envío.
- RF-18. El sistema debe permitir listar las fotos de una propiedad y ver cada una.
- RF-19. El sistema debe permitir crear una publicación para una propiedad, eligiendo la red (Instagram o Facebook), el formato (imagen) y, de forma opcional, indicaciones, estilo de imagen, texto para la imagen y las fotos de la propiedad a usar.
- RF-20. Las fotos elegidas tienen que pertenecer a esa propiedad.
- RF-21. Toda publicación nueva arranca en estado borrador.
- RF-22. El sistema debe generar con IA el texto del post (caption con emojis y hashtags) a partir de los datos de la propiedad y las indicaciones. Al generarlo, la publicación pasa a estado lista.
- RF-23. El sistema debe permitir volver a generar el texto mientras la publicación no esté publicada.
- RF-24. El sistema debe sugerir con IA un texto corto para poner sobre la imagen.
- RF-25. El sistema debe permitir cambiar a mano el texto de la imagen y el estilo.
- RF-26. El sistema debe generar con IA una imagen cuadrada para la red elegida a partir de las fotos elegidas, con el texto de la imagen, la operación, los ambientes y el precio. Debe mejorar luz y colores sin cambiar la propiedad.
- RF-27. Para generar la imagen, la publicación tiene que tener al menos una foto elegida y no estar publicada.
- RF-28. La imagen generada se guarda y queda asociada a la publicación, para poder verla y descargarla.
- RF-29. El sistema debe permitir listar las publicaciones, ver el detalle de cada una y borrarlas.
- RF-30. El sistema debe permitir marcar una publicación como publicada, solo si está en estado lista. Se guarda la fecha y ya no se puede volver a generar.
- RF-31. Si la IA falla o tarda demasiado, el sistema debe devolver un mensaje de error claro sin perder la publicación.
- RF-32. Solo un usuario con rol admin puede usar las funciones de administración. Un usuario común recibe error 403.
- RF-33. El admin debe poder listar todas las inmobiliarias de la plataforma.
- RF-34. El admin debe poder listar, ver, crear, editar y borrar usuarios.
- RF-35. El admin debe poder bloquear un usuario. Un usuario bloqueado no puede iniciar sesión ni renovar su token.
- RF-36. El admin debe poder volver a activar un usuario bloqueado.

**Pendiente:** videos y página personalizada para cada inmobiliaria.

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
