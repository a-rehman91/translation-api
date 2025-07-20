# 🌍 Translation API

A Spring Boot-based Translation API with support for tagging, JWT authentication, Swagger documentation, and PostgreSQL. The application is Dockerized for seamless deployment.

---

## 🚀 Features

- Add, update, delete translations
- Assign tags to translations (many-to-many)
- Search translations by key, locale, or tags
- Secure endpoints using JWT authentication
- Swagger UI for API exploration
- PostgreSQL as backend database
- Docker + Docker Compose setup for easy bootstrapping

---

## 🧱 Tech Stack

- Java 17
- Spring Boot 3
- PostgreSQL
- Docker + Docker Compose
- Swagger/OpenAPI
- HikariCP + Hibernate
- JUnit 5

---

## 🛠️ Setup Instructions

### 🔧 Prerequisites

- Docker & Docker Compose installed
- Java 17 and Maven (for local development, optional)

### 📦 Build & Run with Docker

```bash
# Step 1: Package the app
mvn clean package -DskipTests

# Step 2: Build and run containers
docker-compose up --build
```

### ✅ Verify

Once running:

- API Base URL: `http://localhost:8881/v1`
- Swagger UI: `http://localhost:8881/v1/swagger-ui/index.html`

---

## 🔑 Authentication

Most endpoints require a JWT token.

### Sample Header:
```
Authorization: Bearer <your-jwt-token>
```

Use `/auth/login` or your custom method to generate tokens.

---

## 🗂️ API Structure

| Resource    | Endpoint                 | Method |
|-------------|--------------------------|--------|
| Translation | `/translations`          | GET/POST/PUT/DELETE |
| Tag         | `/tags`                  | GET/POST/PUT/DELETE |
| Swagger     | `/swagger-ui/index.html` | GET |
| Auth        | `/auth/login`            | POST |

---

## 🧠 Design Choices

- **UUIDs** for global uniqueness across tables
- **Composite key** for many-to-many `translation_tag`
- **Swagger** for interactive API documentation
- **JWT** for secure, stateless auth
- **Environment variables** for DB config (in `docker-compose.yml`)
- **Docker** for unified environment across local/dev/prod

---

## 🧪 Running Tests

```bash
# Unit & Feature tests
mvn test
```
