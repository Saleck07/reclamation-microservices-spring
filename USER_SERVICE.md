# User Service Documentation

## Overview
User Service is a microservice responsible for managing user information. It provides CRUD operations for users and serves as a data source for other services that need user information.

## Technical Details

### Port
- **Port**: 8081
- **Base URL**: http://localhost:8081
- **API Base Path**: `/api/users`

### Technology Stack
- **Framework**: Spring Boot 4.0.1
- **Java Version**: 17
- **Spring Cloud Version**: 2025.1.0
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA
- **Service Discovery**: Netflix Eureka Client

### Dependencies
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-web`
- `spring-cloud-starter-netflix-eureka-client`
- `postgresql` (PostgreSQL driver)
- `lombok`

## Database Configuration

### PostgreSQL Setup
- **Database Name**: `userdb`
- **Username**: `postgres`
- **Password**: `babe`
- **Connection URL**: `jdbc:postgresql://localhost:5432/userdb`

### Database Schema
The service uses JPA with Hibernate auto-update:
- **DDL Mode**: `update` (automatically creates/updates tables)

### User Entity
```java
@Entity
@Table(name = "users")
public class User {
    private Long id;              // Primary key, auto-generated
    private String nom;           // User name (required)
    private String email;         // Email (required, unique)
    private String telephone;    // Phone number (required)
    private LocalDateTime createdAt; // Auto-set on creation
}
```

## API Endpoints

### 1. Create User
**POST** `/api/users`

**Request Body:**
```json
{
  "nom": "John Doe",
  "email": "john.doe@example.com",
  "telephone": "1234567890"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "nom": "John Doe",
  "email": "john.doe@example.com",
  "telephone": "1234567890",
  "createdAt": "2026-01-20T22:46:50.231828711"
}
```

**Error:** `400 Bad Request` - If email already exists

### 2. Get All Users
**GET** `/api/users`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "nom": "John Doe",
    "email": "john.doe@example.com",
    "telephone": "1234567890",
    "createdAt": "2026-01-20T22:46:50.231828711"
  }
]
```

### 3. Get User by ID
**GET** `/api/users/{id}`

**Response:** `200 OK`
```json
{
  "id": 1,
  "nom": "John Doe",
  "email": "john.doe@example.com",
  "telephone": "1234567890",
  "createdAt": "2026-01-20T22:46:50.231828711"
}
```

**Error:** `404 Not Found` - If user doesn't exist

### 4. Get User by Email
**GET** `/api/users/email/{email}`

**Response:** `200 OK`
```json
{
  "id": 1,
  "nom": "John Doe",
  "email": "john.doe@example.com",
  "telephone": "1234567890",
  "createdAt": "2026-01-20T22:46:50.231828711"
}
```

**Error:** `404 Not Found` - If user doesn't exist

### 5. Check User Exists
**GET** `/api/users/{id}/exists`

**Response:** `200 OK`
```json
true
```

This endpoint is used by other services (like Reclamation Service) to verify user existence.

## Service Layer

### UserService
Main service class handling business logic:
- **createUser()**: Creates a new user with email uniqueness validation
- **getAllUsers()**: Retrieves all users
- **getUserById()**: Retrieves user by ID
- **getUserByEmail()**: Retrieves user by email
- **userExists()**: Checks if user exists (used by other services)

## Repository Layer

### UserRepository
Extends `JpaRepository<User, Long>` with custom methods:
- `findByEmail(String email)`: Find user by email
- `existsByEmail(String email)`: Check if email exists

## Running the Service

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL running on localhost:5432
- Database `userdb` created (or auto-created)

### Start Command
```bash
cd userservice
mvn spring-boot:run
```

### Database Setup
```sql
CREATE DATABASE userdb;
```

Or the service will attempt to create it automatically if PostgreSQL allows.

## Integration with Other Services

### Used By
- **Reclamation Service**: Verifies user existence before creating reclamations
- **API Gateway**: Routes user-related requests to this service

### Service Discovery
The service registers itself with Eureka Server as `user-service` and can be discovered by other services using this name.

## Example Usage

### Create a User
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "John Doe",
    "email": "john.doe@example.com",
    "telephone": "1234567890"
  }'
```

### Get All Users
```bash
curl http://localhost:8081/api/users
```

### Get User by ID
```bash
curl http://localhost:8081/api/users/1
```

### Check User Exists
```bash
curl http://localhost:8081/api/users/1/exists
```

## Error Handling

- **400 Bad Request**: Invalid input or duplicate email
- **404 Not Found**: User not found
- **500 Internal Server Error**: Database or server errors

## Logging

The service uses SLF4J with Lombok's `@Slf4j` annotation for logging. All operations are logged at INFO level, errors at ERROR level.
