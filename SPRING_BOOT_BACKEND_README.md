# Spring Boot Backend for Angular Blog Application

## Overview

This is the backend API for the Angular Blog application. The frontend runs at `http://localhost:4200` and expects this backend at:

```
http://localhost:8012/dempproject/blogger/
```

---

## Tech Stack

- Java 11+
- Spring Boot 2.x
- Spring Security + JWT
- Spring Data JPA
- MySQL / PostgreSQL
- Maven

---

## Project Setup

### 1. Maven Dependencies (`pom.xml`)

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
        <version>0.9.1</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>
```

### 2. `application.properties`

```properties
server.port=8012
server.servlet.context-path=/dempproject/blogger

spring.datasource.url=jdbc:mysql://localhost:3306/blog_db
spring.datasource.username=root
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3. CORS Configuration

Allow requests from the Angular frontend:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

---

## Database Entities

### User

```java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String firstName;   // mapped as first_name
    private String lastName;
}
```

### Blog

```java
@Entity
@Table(name = "blogs")
public class Blog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String image;
    private boolean isFeatured;   // is_featured
    private boolean isActive;     // is_active
    private Long userId;          // user_id (FK to users)
    private LocalDateTime createdAt;  // created_at
}
```

### Category

```java
@Entity
@Table(name = "categories")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
```

### Page (CMS)

```java
@Entity
@Table(name = "pages")
public class Page {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String slug;
}
```

### Contact

```java
@Entity
@Table(name = "contacts")
public class Contact {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String message;
}
```

---

## API Endpoints

All endpoints are prefixed with `/api`.

### Authentication

| Method | Endpoint     | Description              | Auth Required |
|--------|--------------|--------------------------|---------------|
| POST   | `/api/login` | Login and receive JWT    | No            |

**Request Body:**
```json
{
  "username": "admin",
  "password": "password"
}
```

**Response:**
```json
[
  {
    "token": "<jwt_token>",
    "first_name": "John"
  }
]
```

> The Angular interceptor sends the token as `Authorization: <token>` header on all authenticated requests.

---

### Public Blog Endpoints

| Method | Endpoint               | Description                        | Auth Required |
|--------|------------------------|------------------------------------|---------------|
| GET    | `/api/blogs`           | Get all active blogs               | No            |
| GET    | `/api/blog/{id}`       | Get single blog by ID              | No            |
| GET    | `/api/featured_blogs`  | Get featured blogs                 | No            |
| GET    | `/api/recent_blogs`    | Get recent blogs                   | No            |
| GET    | `/api/categories`      | Get all categories                 | No            |
| GET    | `/api/page/{slug}`     | Get CMS page by slug               | No            |

---

### Admin Blog Endpoints (JWT Protected)

| Method | Endpoint                  | Description              | Auth Required |
|--------|---------------------------|--------------------------|---------------|
| GET    | `/api/adminBlogs`         | Get all blogs (admin)    | Yes           |
| GET    | `/api/adminBlog/{id}`     | Get blog by ID (admin)   | Yes           |
| POST   | `/api/createBlog`         | Create a new blog        | Yes           |
| POST   | `/api/updateBlog/{id}`    | Update blog by ID        | Yes           |
| DELETE | `/api/deleteBlog/{id}`    | Delete blog by ID        | Yes           |

**Create/Update Blog Request Body:**
```json
{
  "title": "Blog Title",
  "description": "Blog content...",
  "is_featured": true,
  "is_active": true,
  "image": "image_filename.jpg",
  "user_id": 1
}
```

---

### Contact Endpoint

| Method | Endpoint        | Description              | Auth Required |
|--------|-----------------|--------------------------|---------------|
| POST   | `/api/contact/` | Submit contact form      | No            |

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "1234567890",
  "message": "Hello!"
}
```

---

## JWT Security

### Flow

1. Client POSTs credentials to `/api/login`
2. Backend validates credentials, returns JWT token + `first_name`
3. Angular stores token in `localStorage`
4. All subsequent admin requests include `Authorization: <token>` header
5. Spring Security filter validates the token on protected routes

### JWT Filter (Pseudocode)

```java
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) {
        String token = request.getHeader("Authorization");
        if (token != null && jwtUtil.validateToken(token)) {
            // set authentication in SecurityContext
        }
        chain.doFilter(request, response);
    }
}
```

### Security Config

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .cors().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/login", "/api/blogs", "/api/blog/**",
                         "/api/featured_blogs", "/api/recent_blogs",
                         "/api/categories", "/api/page/**", "/api/contact/").permitAll()
            .antMatchers("/api/adminBlogs", "/api/adminBlog/**",
                         "/api/createBlog", "/api/updateBlog/**",
                         "/api/deleteBlog/**").authenticated();
    }
}
```

---

## Response Format

### Blog List Response

```json
[
  {
    "id": 1,
    "title": "My First Blog",
    "short_desc": "Short description...",
    "author": "John",
    "image": "img1.jpg",
    "created_at": "2024-01-01T10:00:00"
  }
]
```

### Create/Update Response

```json
{
  "status": "success",
  "message": "Blog created successfully"
}
```

On error:
```json
{
  "status": "error",
  "message": "Something went wrong"
}
```

---

## Project Package Structure

```
com.blog
├── config/
│   ├── CorsConfig.java
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── BlogController.java
│   ├── CategoryController.java
│   ├── PageController.java
│   └── ContactController.java
├── entity/
│   ├── User.java
│   ├── Blog.java
│   ├── Category.java
│   ├── Page.java
│   └── Contact.java
├── repository/
│   ├── UserRepository.java
│   ├── BlogRepository.java
│   ├── CategoryRepository.java
│   ├── PageRepository.java
│   └── ContactRepository.java
├── service/
│   ├── AuthService.java
│   ├── BlogService.java
│   └── ContactService.java
├── security/
│   ├── JwtUtil.java
│   └── JwtFilter.java
└── BlogApplication.java
```

---

## Running the Application

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

The API will be available at:
```
http://localhost:8012/dempproject/blogger/api/
```

---

## Notes

- The Angular frontend sends the JWT token as a raw string in the `Authorization` header (not `Bearer <token>`). Match this in your JWT filter.
- `is_featured` and `is_active` are boolean flags used to filter blogs on the public frontend.
- The `short_desc` field in the public blog response is a truncated version of `description`.
- Image handling: the Angular form sends image as a file object; consider using `MultipartFile` in Spring Boot or storing a filename string.
- The `/api/contact/` endpoint is called via POST with a trailing slash — ensure your Spring controller mapping matches exactly.
