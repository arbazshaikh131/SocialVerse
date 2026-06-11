# 📱 Social Media REST API

A production-ready **Spring Boot** REST API for a social media platform, featuring JWT authentication, a follow system, posts, comments, likes, full pagination/search, Swagger UI documentation, and Docker support.

---

## 🚀 Features

| Feature | Details |
|---|---|
| **Authentication** | JWT access tokens + refresh tokens, BCrypt password hashing |
| **Users** | Register, login, profile with bio & avatar URL |
| **Follow System** | Follow / unfollow users, follower & following counts |
| **Posts** | Full CRUD, pagination, keyword search |
| **Comments** | Full CRUD, paginated by post or user |
| **Likes** | Like / unlike posts, duplicate like prevention |
| **Pagination** | All list endpoints support `page`, `size`, `sortBy`, `direction` |
| **Search** | Search users by username, posts by keyword |
| **Validation** | Bean Validation (`@NotBlank`, `@Size`) on all request bodies |
| **Error Handling** | Global `@RestControllerAdvice` with structured JSON errors |
| **API Docs** | Swagger UI auto-generated at `/swagger-ui.html` |
| **Docker** | Multi-stage Dockerfile + `docker-compose.yml` for one-command startup |

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.2**
- **Spring Security** (JWT stateless auth)
- **Spring Data JPA** + **Hibernate**
- **MySQL 8**
- **JJWT 0.11.5** (modern, non-deprecated API)
- **SpringDoc OpenAPI 2** (Swagger UI)
- **Lombok**
- **Docker** + **Docker Compose**

---

## 🏗️ Architecture

```
src/main/java/com/example/socialmedia/
├── configuration/     # Security, CORS, OpenAPI beans
├── controllers/       # REST endpoints (thin layer)
├── services/          # Business logic
├── repository/        # Spring Data JPA interfaces
├── entities/          # JPA entities (User, Post, Comment, Like, RefreshToken)
├── requests/          # Input DTOs with validation
├── responses/         # Output DTOs
├── security/          # JWT provider, filter, UserDetails
└── exceptions/        # Custom exceptions + GlobalExceptionHandler
```

**Request flow:** `HTTP Request → JwtAuthenticationFilter → Controller → Service → Repository → MySQL`

---

## ⚡ Quick Start

### Option 1: Docker (recommended)

```bash
# 1. Clone the repository
git clone https://github.com/your-username/social-media-api.git
cd social-media-api

# 2. Configure secrets
cp .env.example .env
# Edit .env — set DB_PASSWORD and JWT_SECRET

# 3. Start everything
docker-compose up --build
```

App: http://localhost:8080  
Swagger: http://localhost:8080/swagger-ui.html

---

### Option 2: Local (MySQL required)

**Prerequisites:** Java 17+, Maven 3.8+, MySQL 8

```bash
# 1. Create the database
mysql -u root -p -e "CREATE DATABASE socialmedia;"

# 2. Configure
cp src/main/resources/application-sample.properties src/main/resources/application.properties
# Edit application.properties — set DB_PASSWORD and JWT_SECRET

# 3. Run
./mvnw spring-boot:run
```

---

## 🔑 Environment Variables

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/socialmedia...` | JDBC connection URL |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `password` | MySQL password |
| `JWT_SECRET` | *(must change)* | HS256 signing key — min 32 characters |
| `JWT_ACCESS_EXPIRES_MS` | `900000` | Access token TTL (15 min) |
| `JWT_REFRESH_EXPIRES_MS` | `604800000` | Refresh token TTL (7 days) |
| `SERVER_PORT` | `8080` | Application port |

---

## 📖 API Reference

Full interactive docs are available at **`/swagger-ui.html`** when the app is running.

### Authentication

```http
POST /auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "password": "secret123"
}
```

```http
POST /auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "secret123"
}
```

**Response:**
```json
{
  "userId": 1,
  "username": "johndoe",
  "accessToken": "Bearer eyJhbG...",
  "refreshToken": "550e8400-e29b..."
}
```

Use the `accessToken` value in subsequent requests:
```http
Authorization: Bearer eyJhbG...
```

---

### Users

```http
GET    /users?page=0&size=10&sortBy=username&direction=asc
GET    /users/search?username=john
GET    /users/{id}
PUT    /users/{id}                    # update bio / profileImageUrl
DELETE /users/{id}

POST   /users/{id}/follow/{targetId}
DELETE /users/{id}/follow/{targetId}

GET    /users/{id}/activity
```

### Posts

```http
GET    /posts?page=0&size=10&sortBy=createdAt&direction=desc
GET    /posts?userId=1
GET    /posts/search?keyword=spring
GET    /posts/{id}
POST   /posts
PUT    /posts/{id}
DELETE /posts/{id}
```

### Comments

```http
GET    /comments?postId=1&page=0&size=10
GET    /comments/{id}
POST   /comments
PUT    /comments/{id}
DELETE /comments/{id}
```

### Likes

```http
GET    /likes?postId=1
GET    /likes/{id}
POST   /likes
DELETE /likes/{id}
```

### Token Refresh

```http
POST /auth/refresh
Content-Type: application/json

{
  "userId": 1,
  "refreshToken": "550e8400-e29b..."
}
```

---

## 🔒 Security Notes

- Passwords hashed with **BCrypt** (strength 10)
- JWT signed with **HS256** using a configurable secret (min 256-bit)
- Access tokens expire in **15 minutes** by default
- Refresh tokens expire in **7 days** by default
- CORS is enabled — configure `allowedOriginPatterns` for production
- Like table has a **unique constraint** preventing duplicate likes
- No credentials or secrets committed to source code — all via env vars

---

## 🗄️ Database Schema (auto-created by Hibernate)

```
users          → id, username, password, bio, profile_image_url
post           → id, user_id, title, text, created_at
comment        → id, user_id, post_id, text, created_at
post_like      → id, user_id, post_id  (unique: user_id + post_id)
refresh_token  → id, user_id, token, expiry_date
user_follows   → follower_id, following_id
```

---

## 🚧 Future Enhancements

- [ ] Role-based access control (Admin / Moderator roles)
- [ ] Endpoint-level ownership checks (`@PreAuthorize`)
- [ ] Story / reel media support (S3 file uploads)
- [ ] Real-time notifications with WebSockets
- [ ] Direct messaging
- [ ] Post hashtag and mention indexing
- [ ] Rate limiting with Bucket4j
- [ ] Redis caching for hot feeds
- [ ] Integration tests with Testcontainers
- [ ] CI/CD pipeline (GitHub Actions)

---

## 📄 License

MIT License — free to use, modify, and distribute.
