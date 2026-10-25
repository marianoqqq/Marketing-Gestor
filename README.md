# Inmo Marketing API 🏡✨

> **API REST para inmobiliarias:** Gestión de propiedades, fotografías y generación automatizada de contenido publicitario para redes sociales con Inteligencia Artificial.

---

## 📖 Tabla de Contenidos
- [Descripción General](#-descripción-general)
- [Arquitectura del Sistema](#-arquitectura-del-sistema)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Requisitos Funcionales (MVP)](#-requisitos-funcionales-mvp)
- [Requisitos Previos y Setup Local](#-requisitos-previos-y-setup-local)
- [Variables de Entorno](#-variables-de-entorno)
- [Despliegue en Render con Docker](#-despliegue-en-render-con-docker)
- [Documentación de Endpoints (API Reference)](#-documentación-de-endpoints-api-reference)
- [Swagger / OpenAPI](#-swagger--openapi)

---

## 🌟 Descripción General

**Inmo Marketing API** es una solución backend integral construida para inmobiliarias y corredores inmobiliarios. Permite centralizar la gestión de inmuebles, administrar su catálogo fotográfico en alta resolución y generar de forma automatizada piezas publicitarias completas para redes sociales (Instagram y Facebook).

Mediante la integración de modelos de **Inteligencia Artificial de última generación (Groq y OpenAI)**, la plataforma analiza los atributos de cada propiedad para:
1. Redactar copys persuasivos optimizados para redes sociales con ganchos, emojis y hashtags estratégicos.
2. Sugerir frases de alto impacto publicitario para sobreimprimir en flyers e imágenes.
3. Generar y enriquecer imágenes publicitarias a partir de las fotografías reales del inmueble.

---

## 🏗 Arquitectura del Sistema

El proyecto sigue una arquitectura en capas (**Layered Architecture**) basada en las mejores prácticas de **Spring Boot**, promoviendo un desacoplamiento limpio, alta cohesión y testeabilidad:

```
inmo-marketing-api/
├── src/main/java/com/inmomarketing/api/
│   ├── config/              # Seguridad (Spring Security, JWT, CORS) y OpenAPI
│   ├── controller/          # Endpoints REST (Controladores HTTP)
│   ├── dto/                 # Data Transfer Objects (Request y Response desacoplados)
│   ├── enums/               # Enumeraciones de dominio (EstadoPropiedad, RedSocial, etc.)
│   ├── exception/           # Manejador global de excepciones (@RestControllerAdvice)
│   ├── mapper/              # Mapeadores entre entidades y DTOs
│   ├── model/               # Entidades de persistencia JPA / Hibernate
│   ├── repository/          # Interfaces Spring Data JPA
│   ├── securityJwt/         # Filtros y validadores de tokens JWT
│   └── service/             # Lógica de negocio y clientes de IA (Groq, OpenAI)
├── src/main/resources/
│   ├── application.properties             # Configuración centralizada
│   ├── application-local.properties.example # Plantilla de configuración local
│   └── application-h2.properties          # Perfil de base de datos H2
├── Dockerfile               # Multi-stage Dockerfile para compilación y despliegue
├── .dockerignore            # Exclusiones de contexto para optimizar Docker builds
└── pom.xml                  # Definición del proyecto Maven y dependencias
```

### Características de Diseño:
- **Seguridad Stateless con JWT:** Autenticación basada en tokens de acceso (Access Tokens) y de renovación (Refresh Tokens) sin estado en servidor.
- **Control de Acceso Basado en Roles (RBAC):** Separación estricta de privilegios con `@PreAuthorize` para roles `ROLE_USER` y `ROLE_ADMIN`.
- **Aislamiento Multi-inquilino (Multi-tenant lógico):** Cada inmobiliaria sólo accede y manipula sus propias propiedades, fotos y publicaciones.
- **Manejo Centralizado de Errores:** Respuestas de error homogéneas con códigos HTTP estándar y mensajes claros.

---

## 🚀 Tecnologías Utilizadas

| Categoría | Tecnología / Librería | Versión / Detalle |
|---|---|---|
| **Lenguaje** | Java | **OpenJDK 21 (LTS)** |
| **Framework** | Spring Boot | **4.0.6** (Spring WebMVC, Spring Data JPA, Spring Security) |
| **Inteligencia Artificial** | Spring AI | **2.0.0-RC1** |
| **Motor LLM (Texto)** | Groq Cloud API | Modelo `openai/gpt-oss-120b` (o `llama-3.3-70b-versatile`) |
| **Motor de Imagen** | OpenAI Images API | Modelo `gpt-image-2.5-sunburst` / DALL-E |
| **Persistencia** | PostgreSQL | Neon Serverless Postgres / Hibernate ORM |
| **Base de Datos Local** | H2 Database | Base en memoria para desarrollo rápido y pruebas |
| **Seguridad & Tokens** | JJWT | **0.12.6** (Auth Bearer Token Stateless) |
| **Documentación** | Springdoc OpenAPI | **3.0.3** (Swagger UI interactivo) |
| **Contenedores** | Docker | Build Multi-stage con **Eclipse Temurin 21 Alpine** |
| **Build Tool** | Apache Maven | Maven 3.9+ con Maven Wrapper (`./mvnw`) |

---

## 📋 Requisitos Funcionales (MVP)

- **RF-01.** El sistema debe permitir registrarse con nombre, email, contraseña y nombre de la inmobiliaria. Al registrarse se crea la inmobiliaria y el usuario queda asociado a ella con rol usuario.
- **RF-02.** El sistema no debe permitir registrar un email que ya existe.
- **RF-03.** El sistema debe exigir una contraseña de al menos 8 caracteres.
- **RF-04.** El sistema debe permitir iniciar sesión con email y contraseña y devolver un token de acceso y un token de renovación.
- **RF-05.** El sistema debe permitir renovar el token de acceso usando el token de renovación, sin volver a pedir la contraseña.
- **RF-06.** El sistema debe rechazar con error 401 los pedidos sin token, con token vencido o con token inválido.
- **RF-07.** El sistema no debe dejar iniciar sesión a un usuario bloqueado o inactivo.
- **RF-08.** Cada usuario solo puede ver y modificar los datos de su propia inmobiliaria.
- **RF-09.** El sistema debe permitir ver los datos de la propia inmobiliaria: nombre, teléfono, Instagram, dirección y logo.
- **RF-10.** El sistema debe permitir editar esos datos. El nombre es obligatorio (hasta 50 caracteres), el teléfono hasta 30, el Instagram hasta 15 y la dirección hasta 255.
- **RF-11.** Los datos de la inmobiliaria se muestran en el panel. Por ahora la IA no los usa: los textos e imágenes se arman solo con los datos de la propiedad y de la publicación.
- **RF-12.** El sistema debe permitir cargar una propiedad con título, tipo (casa, departamento, PH, terreno o local), operación (venta o alquiler), estado, precio, dirección, ambientes, metros cuadrados y descripción.
- **RF-13.** Son obligatorios el título (hasta 150 caracteres), el tipo, la operación y el precio. El precio y los metros cuadrados deben ser mayores a 0, y los ambientes al menos 1.
- **RF-14.** El sistema debe permitir listar las propiedades de la inmobiliaria y ver el detalle de cada una.
- **RF-15.** El sistema debe permitir editar una propiedad y cambiar su estado entre disponible, reservada y vendida.
- **RF-16.** El sistema debe permitir borrar una propiedad.
- **RF-17.** El sistema debe permitir subir una o varias fotos a una propiedad. Solo acepta JPG, PNG o WEBP, de hasta 5 MB cada una y 25 MB por envío.
- **RF-18.** El sistema debe permitir listar las fotos de una propiedad y ver cada una.
- **RF-19.** El sistema debe permitir crear una publicación para una propiedad, eligiendo la red (Instagram o Facebook), el formato (imagen) y, de forma opcional, indicaciones, estilo de imagen, texto para la imagen y las fotos de la propiedad a usar.
- **RF-20.** Las fotos elegidas tienen que pertenecer a esa propiedad.
- **RF-21.** Toda publicación nueva arranca en estado borrador.
- **RF-22.** El sistema debe generar con IA el texto del post (caption con emojis y hashtags) a partir de los datos de la propiedad y las indicaciones. Al generarlo, la publicación pasa a estado lista.
- **RF-23.** El sistema debe permitir volver a generar el texto mientras la publicación no esté publicada.
- **RF-24.** El sistema debe sugerir con IA un texto corto para poner sobre la imagen.
- **RF-25.** El sistema debe permitir cambiar a mano el texto de la imagen y el estilo.
- **RF-26.** El sistema debe generar con IA una imagen cuadrada para la red elegida a partir de las fotos elegidas, con el texto de la imagen, la operación, los ambientes y el precio. Debe mejorar luz y colores sin cambiar la propiedad.
- **RF-27.** Para generar la imagen, la publicación tiene que tener al menos una foto elegida y no estar publicada.
- **RF-28.** La imagen generada se guarda y queda asociada a la publicación, para poder verla y descargarla.
- **RF-29.** El sistema debe permitir listar las publicaciones, ver el detalle de cada una y borrarlas.
- **RF-30.** El sistema debe permitir marcar una publicación como publicada, solo si está en estado lista. Se guarda la fecha y ya no se puede volver a generar.
- **RF-31.** Si la IA falla o tarda demasiado, el sistema debe devolver un mensaje de error claro sin perder la publicación.
- **RF-32.** Solo un usuario con rol admin puede usar las funciones de administración. Un usuario común recibe error 403.
- **RF-33.** El admin debe poder listar todas las inmobiliarias de la plataforma.
- **RF-34.** El admin debe poder listar, ver, crear, editar y borrar usuarios.
- **RF-35.** El admin debe poder bloquear un usuario. Un usuario bloqueado no puede iniciar sesión ni renovar su token.
- **RF-36.** El admin debe poder volver a activar un usuario bloqueado.

---

## 💻 Requisitos Previos y Setup Local

### Requisitos del Sistema
- **Java 21 JDK** instalado (`java -version`).
- **Git** instalado.
- **PostgreSQL** local (opcional) o base de datos serverless en la nube como [Neon.tech](https://neon.tech).
- Clave de API de [Groq](https://console.groq.com) para generación de textos.
- Clave de API de [OpenAI](https://platform.openai.com) (opcional, para generación de imágenes).

### 1. Clonar el repositorio
```bash
git clone https://github.com/marianoqqq/Marketing-Gestor.git
cd Marketing-Gestor
```

### 2. Configurar variables de entorno o archivo local

**Opción A: Archivo local (Recomendado para desarrollo)**
Copiá el archivo de ejemplo a `application-local.properties` (ignorado por Git):
```bash
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
```
Editá `src/main/resources/application-local.properties` con tus credenciales:
```properties
jwt.secret=tu_clave_secreta_en_base64_de_minimo_256_bits
spring.ai.openai.api-key=gsk_tu_api_key_de_groq
spring.ai.openai.chat.options.model=openai/gpt-oss-120b

spring.datasource.url=jdbc:postgresql://ep-xxxx.sa-east-1.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=tu_password_de_neon
```

**Opción B: Variables de Entorno del Sistema**
Podés definir las variables directamente en tu terminal:
```bash
# En Windows (PowerShell)
$env:PORT="8080"
$env:DB_URL="jdbc:postgresql://localhost:5432/inmo_marketing_db"
$env:DB_USER="postgres"
$env:DB_PASSWORD="mi_password"
$env:JWT_SECRET="tu_jwt_secret"
$env:GROQ_API_KEY="gsk_..."

# En Linux / macOS
export PORT=8080
export DB_URL="jdbc:postgresql://localhost:5432/inmo_marketing_db"
export DB_USER="postgres"
export DB_PASSWORD="mi_password"
export JWT_SECRET="tu_jwt_secret"
export GROQ_API_KEY="gsk_..."
```

### 3. Ejecutar la aplicación

**En Windows:**
```bash
.\mvnw.cmd spring-boot:run
```

**En Linux / macOS:**
```bash
./mvnw spring-boot:run
```

La aplicación iniciará en `http://localhost:8080`.

### 4. Ejecutar con Docker localmente (opcional)
```bash
# Construir la imagen local
docker build -t inmo-marketing-api .

# Ejecutar el contenedor
docker run -p 8080:8080 \
  -e PORT=8080 \
  -e DB_URL="jdbc:postgresql://host.docker.internal:5432/inmo_marketing_db" \
  -e DB_USER="postgres" \
  -e DB_PASSWORD="secretpassword" \
  -e JWT_SECRET="clave_secreta_base64" \
  -e GROQ_API_KEY="gsk_..." \
  inmo-marketing-api
```

---

## 🔑 Variables de Entorno

La aplicación parametriza todas las conexiones externas a través de variables de entorno, manteniendo valores por defecto locales para no romper la ejecución de desarrollo:

| Variable | Descripción | Valor por defecto (Local) | Requerida en Render / Prod |
|---|---|---|:---:|
| `PORT` | Puerto HTTP en el que escucha el servidor Spring Boot. Render asigna este valor dinámicamente. | `8080` | **Sí** (automático en Render) |
| `DB_URL` | URL JDBC completa de conexión a PostgreSQL. **Debe incluir obligatoriamente el prefijo `jdbc:`** (ej. `jdbc:postgresql://host:port/database?sslmode=require`). | `jdbc:postgresql://localhost:5432/inmo_marketing_db` | **Sí** |
| `DB_USER` | Nombre de usuario para la conexión a PostgreSQL. | `postgres` | **Sí** |
| `DB_PASSWORD` | Contraseña del usuario de la base de datos PostgreSQL. | *(vacío)* | **Sí** |
| `JWT_SECRET` | Clave secreta en Base64 de al menos 256 bits (32 caracteres) para firmar y validar tokens JWT. | *(vacío)* | **Sí** |
| `GROQ_API_KEY` | API Key de Groq para generación de textos de publicaciones y ganchos publicitarios con IA. | *(vacío)* | **Sí** |
| `GROQ_MODEL` | Nombre del modelo LLM de Groq a utilizar. | `openai/gpt-oss-120b` | No |
| `OPENAI_API_KEY` | API Key de OpenAI para generación y retoque de imágenes de propiedades. | *(vacío)* | Opcional |
| `SPRING_PROFILES_ACTIVE` | Perfil activo de Spring Boot (`local`, `prod`, etc.). | `local` | No |

> ⚠️ **IMPORTANTE SOBRE `DB_URL`:**
> Render o proveedores externos como Neon suelen proveer la cadena de conexión en formato URI estándar (ej. `postgresql://user:pass@ep-xyz.tech/neondb`).
> **En Spring Boot con JDBC, SIEMPRE se debe anteponer `jdbc:`** al inicio del enlace:
> ```
> jdbc:postgresql://ep-xxxx.sa-east-1.aws.neon.tech/neondb?sslmode=require
> ```

---

## ☁️ Despliegue en Render con Docker

Render permite desplegar el backend empaquetado en contenedores Docker de forma rápida y automatizada.

### Paso 1: Subir cambios a GitHub
Asegurate de que los cambios (`Dockerfile`, `.dockerignore`, `application.properties`) estén subidos a la rama principal (`main`) de tu repositorio en GitHub.

### Paso 2: Crear el Web Service en Render
1. Ingresá a tu cuenta de [Render Dashboard](https://dashboard.render.com).
2. Hacé clic en **New +** y seleccioná **Web Service**.
3. Conectá tu repositorio de GitHub: `marianoqqq/Marketing-Gestor`.

### Paso 3: Configurar el servicio
- **Name:** `inmo-marketing-api` (o el nombre que prefieras).
- **Region:** Seleccioná la región más cercana a tu base de datos (ej. *Oregon* o *Frankfurt*).
- **Branch:** `main`.
- **Root Directory:** *(dejar vacío para usar la raíz)*.
- **Runtime / Environment:** Seleccionar **Docker**.
  - Render detectará automáticamente el archivo `Dockerfile` multi-stage presente en la raíz del proyecto.
- **Instance Type:** Podés usar el plan **Free** o superior.

### Paso 4: Cargar las Variables de Entorno (Environment Variables)
En la sección **Environment Variables** del servicio en Render, agregá las siguientes claves con sus valores reales:

| Key | Value (Ejemplo) |
|---|---|
| `DB_URL` | `jdbc:postgresql://ep-xxxx.sa-east-1.aws.neon.tech/neondb?sslmode=require` |
| `DB_USER` | `neondb_owner` |
| `DB_PASSWORD` | `tu_password_de_neon_aqui` |
| `JWT_SECRET` | `dGhpcy1pcy1hLXNlY3VyZS1qd3Qtc2VjcmV0LWtleS1leGFtcGxlLTI1Ni1iaXRzCg==` |
| `GROQ_API_KEY` | `gsk_xxxxxxxxxxxxxxxxxxxxxx` |
| `OPENAI_API_KEY` | `sk-proj-xxxxxxxxxxxxxxxxxxxx` *(opcional si usás imágenes)* |

*(Nota: Render inyecta automáticamente la variable `PORT`, la cual es consumida por `server.port=${PORT:8080}` de Spring Boot).*

### Paso 5: Desplegar
1. Hacé clic en **Deploy Web Service**.
2. Render iniciará la construcción del contenedor Docker:
   - Descarga de dependencias Maven.
   - Compilación con Java 21 y omitiendo tests (`-DskipTests`).
   - Creación de la imagen runtime ligera sobre Alpine JRE 21.
3. Al finalizar, la consola mostrará el mensaje de inicio de Spring Boot y el enlace público asignado (ej. `https://inmo-marketing-api.onrender.com`).

---

## 📡 Documentación de Endpoints (API Reference)

Todos los endpoints (excepto la ruta `/auth/**`) requieren la cabecera HTTP de autorización:
```http
Authorization: Bearer <accessToken>
```

---

### 1. 🔐 Autenticación (`/auth`)
*Endpoints públicos para registro e inicio de sesión.*

#### `POST /auth/register`
Registra un nuevo usuario en el sistema y crea automáticamente la Inmobiliaria asociada.
- **Acceso:** Público
- **Request Body:**
```json
{
  "nombre": "Mariano Pérez",
  "email": "mariano@inmobiliaria.com",
  "password": "PasswordSegura123",
  "nombreInmobiliaria": "Inmobiliaria Norte"
}
```
- **Respuesta:** `201 Created`

#### `POST /auth`
Inicia sesión validando credenciales. Devuelve el token de acceso y de renovación.
- **Acceso:** Público
- **Request Body:**
```json
{
  "email": "mariano@inmobiliaria.com",
  "password": "PasswordSegura123"
}
```
- **Respuesta:** `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### `POST /auth/refresh`
Genera un nuevo token de acceso a partir de un refresh token válido sin requerir contraseña.
- **Acceso:** Público
- **Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
- **Respuesta:** `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 2. 🏢 Inmobiliaria (`/api/inmobiliaria`)
*Gestión del perfil corporativo de la inmobiliaria del usuario autenticado.*
- **Permisos requeridos:** `ROLE_USER`

#### `GET /api/inmobiliaria`
Obtiene los datos de la inmobiliaria vinculada al usuario autenticado.
- **Respuesta:** `200 OK`
```json
{
  "id": 1,
  "nombre": "Inmobiliaria Norte",
  "telefono": "+54 11 4455-6677",
  "instagram": "inmonorte",
  "direccion": "Av. Cabildo 2200, Belgrano, CABA",
  "logoUrl": "https://midominio.com/logo.png"
}
```

#### `PUT /api/inmobiliaria`
Actualiza los datos de la inmobiliaria del usuario autenticado.
- **Request Body:**
```json
{
  "nombre": "Inmobiliaria Norte Central",
  "telefono": "+54 11 4455-8899",
  "instagram": "inmonortear",
  "direccion": "Av. Cabildo 2500, Belgrano, CABA",
  "logoUrl": "https://midominio.com/nuevo-logo.png"
}
```
- **Respuesta:** `200 OK` (objeto `InmobiliariaResponse` actualizado)

---

### 3. 🏠 Propiedades (`/api/propiedades`)
*Administración del inventario de inmuebles de la inmobiliaria.*
- **Permisos requeridos:** `ROLE_USER`

#### `POST /api/propiedades`
Crea una nueva propiedad asociada a la inmobiliaria.
- **Request Body:**
```json
{
  "titulo": "Departamento 3 ambientes con balcón aterrazado",
  "tipo": "DEPARTAMENTO",
  "operacion": "VENTA",
  "estado": "DISPONIBLE",
  "precio": 185000.00,
  "direccion": "Av. Libertador 4200, Palermo",
  "ambientes": 3,
  "metrosCuadrados": 78.5,
  "descripcion": "Luminoso departamento al frente con cochera fija y amenities."
}
```
> **Enums admitidos:**
> - `tipo`: `CASA`, `DEPARTAMENTO`, `PH`, `TERRENO`, `LOCAL`
> - `operacion`: `VENTA`, `ALQUILER`
> - `estado`: `DISPONIBLE`, `RESERVADA`, `VENDIDA`
- **Respuesta:** `201 Created`
```json
{
  "id": 10,
  "titulo": "Departamento 3 ambientes con balcón aterrazado",
  "tipo": "DEPARTAMENTO",
  "operacion": "VENTA",
  "estado": "DISPONIBLE",
  "precio": 185000.00,
  "direccion": "Av. Libertador 4200, Palermo",
  "ambientes": 3,
  "metrosCuadrados": 78.5,
  "descripcion": "Luminoso departamento al frente con cochera fija y amenities.",
  "inmobiliariaId": 1
}
```

#### `GET /api/propiedades`
Lista todas las propiedades de la inmobiliaria del usuario autenticado.
- **Respuesta:** `200 OK` (Lista de objetos `PropiedadResponse`)

#### `GET /api/propiedades/{id}`
Obtiene el detalle de una propiedad específica por su identificador.
- **Respuesta:** `200 OK` (Objeto `PropiedadResponse`)

#### `PUT /api/propiedades/{id}`
Modifica los datos y/o estado de una propiedad existente.
- **Request Body:** Igual que en `POST /api/propiedades`.
- **Respuesta:** `200 OK`

#### `DELETE /api/propiedades/{id}`
Elimina una propiedad del catálogo.
- **Respuesta:** `204 No Content`

---

### 4. 📸 Fotografías y Archivos (`/api`)
*Carga y gestión de archivos multimedia vinculados a las propiedades.*
- **Permisos requeridos:** `ROLE_USER`

#### `POST /api/propiedades/{id}/fotos`
Sube una o varias fotografías a la propiedad indicada.
- **Content-Type:** `multipart/form-data`
- **Parámetro del formulario:** `fotos` (archivos `JPG`, `PNG` o `WEBP`, máx. 5MB por foto y 25MB por envío).
- **Respuesta:** `201 Created`
```json
[
  {
    "id": 15,
    "tipo": "FOTO_PROPIEDAD",
    "nombreOriginal": "living_principal.jpg",
    "contentType": "image/jpeg",
    "url": "/api/archivos/15"
  }
]
```

#### `GET /api/propiedades/{id}/fotos`
Lista las fotos subidas correspondientes a la propiedad indicada.
- **Respuesta:** `200 OK` (Lista de `ArchivoResponse`)

#### `GET /api/archivos/{id}`
Descarga o visualiza directamente el archivo binario almacenado.
- **Respuesta:** `200 OK` (Flujo binario con el `Content-Type` correspondiente, ej. `image/jpeg`).

---

### 5. 🤖 Publicaciones con Inteligencia Artificial (`/api/publicaciones`)
*Flujo completo para la redacción y armado de anuncios para redes sociales asistido por IA.*
- **Permisos requeridos:** `ROLE_USER`

#### Ciclo de Estados de una Publicación:
```
[ BORRADOR ] ──(Generar con IA)──> [ LISTA ] ──(Usuario publica)──> [ PUBLICADA ]
      │                                 ▲
      └──(Error de IA)──> [ ERROR ] ────┘
```

#### `POST /api/publicaciones`
Crea una nueva publicación en estado inicial `BORRADOR`.
- **Request Body:**
```json
{
  "propiedadId": 10,
  "red": "INSTAGRAM",
  "formato": "IMAGEN",
  "indicaciones": "Destacar la vista abierta, luminosidad y cochera",
  "textoImagen": "Oportunidad Palermo",
  "estiloImagen": "Moderno minimalista",
  "fotoIds": [15, 16]
}
```
> **Enums admitidos:**
> - `red`: `INSTAGRAM`, `FACEBOOK`
> - `formato`: `IMAGEN`
- **Respuesta:** `201 Created` (Objeto `PublicacionResponse` con `estado: "BORRADOR"`).

#### `GET /api/publicaciones`
Lista todas las publicaciones creadas por la inmobiliaria.
- **Respuesta:** `200 OK` (Lista de `PublicacionResponse`)

#### `GET /api/publicaciones/{id}`
Obtiene el detalle completo de una publicación.
- **Respuesta:** `200 OK`

#### `DELETE /api/publicaciones/{id}`
Elimina una publicación.
- **Respuesta:** `204 No Content`

#### `POST /api/publicaciones/{id}/sugerir-texto-imagen`
**IA (Groq):** Genera una frase o gancho publicitario corto (hook) de alto impacto para colocar sobre la imagen.
- **Respuesta:** `200 OK`
```json
{
  "publicacionId": 1,
  "textoSugerido": "¡Viví en la mejor zona de Palermo con balcón aterrazado!"
}
```

#### `PATCH /api/publicaciones/{id}/texto-imagen`
Permite editar o sobrescribir manualmente el texto o estilo de la imagen antes de publicarla.
- **Query Params opcionales:** `textoImagen` (string), `estiloImagen` (string)
- **Respuesta:** `200 OK` (Objeto `PublicacionResponse` actualizado)

#### `POST /api/publicaciones/{id}/generar`
**IA (Groq):** Genera automáticamente el texto persuasivo completo (caption con emojis, llamada a la acción y hashtags relevantes) a partir de los atributos de la propiedad y las indicaciones. Cambia el estado a `LISTA`.
- **Respuesta:** `200 OK`
```json
{
  "id": 1,
  "propiedadId": 10,
  "red": "INSTAGRAM",
  "formato": "IMAGEN",
  "indicaciones": "Destacar luminosidad",
  "texto": "✨ ¡Tu próximo hogar en Palermo te espera! ✨\n\nEste increíble departamento de 3 ambientes cuenta con balcón aterrazado, excelente iluminación natural y cochera fija...",
  "textoImagen": "Oportunidad Palermo",
  "estiloImagen": "Moderno minimalista",
  "estado": "LISTA",
  "publicadaEn": null,
  "fotoIds": [15, 16],
  "imagenGeneradaId": null
}
```

#### `POST /api/publicaciones/{id}/generar-imagen`
**IA (OpenAI):** Genera y edita una imagen publicitaria cuadrada (1024x1024) combinando las fotos reales del inmueble con las indicaciones de estilo y textos.
- **Respuesta:** `200 OK` (Objeto `PublicacionResponse` con el ID de la imagen generada)

#### `POST /api/publicaciones/{id}/publicada`
Marca la publicación como finalizada y `PUBLICADA`, registrando la marca de tiempo actual (`publicadaEn`).
- **Respuesta:** `200 OK` (Estado pasa a `PUBLICADA`)

---

### 6. 👥 Administración (`/api/admin` y `/api/users`)
*Funciones exclusivas para usuarios con rol `ROLE_ADMIN`.*

#### Inmobiliarias y Control de Usuarios
- `GET /api/admin/inmobiliarias`: Lista todas las inmobiliarias registradas en la plataforma.
- `PATCH /api/admin/usuarios/{id}/bloquear`: Bloquea a un usuario impidiéndole iniciar sesión o renovar tokens.
- `PATCH /api/admin/usuarios/{id}/activar`: Reactiva a un usuario previamente bloqueado.

#### CRUD de Usuarios (`/api/users`)
- `POST /api/users`: Crea un nuevo usuario directamente desde el panel administrativo.
  ```json
  {
    "email": "agente2@inmonorte.com",
    "password": "Password1234",
    "role": "ROLE_USER"
  }
  ```
- `GET /api/users`: Lista todos los usuarios registrados.
- `GET /api/users/{id}`: Detalle de un usuario por su ID.
- `PUT /api/users/{id}`: Actualiza correo, clave o rol de un usuario.
- `DELETE /api/users/{id}`: Elimina a un usuario del sistema.

---

## 📑 Swagger / OpenAPI

La API cuenta con documentación Swagger UI integrada e interactiva para probar endpoints en tiempo real:

- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html` (o `https://<tu-app-en-render>.onrender.com/swagger-ui/index.html`)
- **OpenAPI JSON Docs:** `http://localhost:8080/v3/api-docs`
