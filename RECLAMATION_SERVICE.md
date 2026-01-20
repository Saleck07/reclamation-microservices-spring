# Reclamation Service Documentation

## Overview
Reclamation Service is a microservice responsible for managing reclamations (complaints/claims). It handles the complete lifecycle of reclamations from creation to resolution, with status tracking and user validation.

## Technical Details

### Port
- **Port**: 8082
- **Base URL**: http://localhost:8082
- **API Base Path**: `/api/reclamations`

### Technology Stack
- **Framework**: Spring Boot 4.0.1
- **Java Version**: 17
- **Spring Cloud Version**: 2025.1.0
- **Database**: MySQL
- **ORM**: Spring Data JPA
- **Service Discovery**: Netflix Eureka Client
- **Inter-Service Communication**: WebClient (Reactive)

### Dependencies
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-web`
- `spring-cloud-starter-netflix-eureka-client`
- `spring-boot-starter-webflux` (for WebClient)
- `mysql-connector-j` (MySQL driver)
- `lombok`

## Database Configuration

### MySQL Setup
- **Database Name**: `reclamationdb`
- **Username**: `root`
- **Password**: (empty by default)
- **Connection URL**: `jdbc:mysql://localhost:3306/reclamationdb?createDatabaseIfNotExist=true`

### Database Schema
The service uses JPA with Hibernate auto-update:
- **DDL Mode**: `update` (automatically creates/updates tables)

### Reclamation Entity
```java
@Entity
@Table(name = "reclamations")
public class Reclamation {
    private Long id;                    // Primary key, auto-generated
    private String titre;               // Title (required)
    private String description;         // Description (required, max 1000 chars)
    private Long userId;                // Foreign key to user (required)
    private StatutReclamation statut;  // Status enum (required)
    private LocalDateTime createdAt;    // Auto-set on creation
    private LocalDateTime updatedAt;    // Auto-updated on modification
}
```

### Status Enum (StatutReclamation)
- **RECUE**: Réclamation reçue (Initial status)
- **EN_COURS**: Réclamation en cours de traitement
- **TRAITEE**: Réclamation traitée (Final status)

## API Endpoints

### 1. Create Reclamation
**POST** `/api/reclamations`

**Request Body:**
```json
{
  "titre": "Problème de connexion",
  "description": "Je ne peux pas me connecter à mon compte depuis hier",
  "userId": 1
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "titre": "Problème de connexion",
  "description": "Je ne peux pas me connecter à mon compte depuis hier",
  "userId": 1,
  "statut": "RECUE",
  "createdAt": "2026-01-20T22:46:50.231828711",
  "updatedAt": "2026-01-20T22:46:50.231828711"
}
```

**Error:** `400 Bad Request` - If user doesn't exist

### 2. Get All Reclamations
**GET** `/api/reclamations`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "titre": "Problème de connexion",
    "description": "Je ne peux pas me connecter à mon compte depuis hier",
    "userId": 1,
    "statut": "RECUE",
    "createdAt": "2026-01-20T22:46:50.231828711",
    "updatedAt": "2026-01-20T22:46:50.231828711"
  }
]
```

### 3. Get Reclamation by ID
**GET** `/api/reclamations/{id}`

**Response:** `200 OK`
```json
{
  "id": 1,
  "titre": "Problème de connexion",
  "description": "Je ne peux pas me connecter à mon compte depuis hier",
  "userId": 1,
  "statut": "RECUE",
  "createdAt": "2026-01-20T22:46:50.231828711",
  "updatedAt": "2026-01-20T22:46:50.231828711"
}
```

**Error:** `404 Not Found` - If reclamation doesn't exist

### 4. Get Reclamations by User ID
**GET** `/api/reclamations/user/{userId}`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "titre": "Problème de connexion",
    "description": "Je ne peux pas me connecter à mon compte depuis hier",
    "userId": 1,
    "statut": "RECUE",
    "createdAt": "2026-01-20T22:46:50.231828711",
    "updatedAt": "2026-01-20T22:46:50.231828711"
  }
]
```

### 5. Get Reclamations by Status
**GET** `/api/reclamations/statut/{statut}`

**Status Values**: `RECUE`, `EN_COURS`, `TRAITEE`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "titre": "Problème de connexion",
    "description": "Je ne peux pas me connecter à mon compte depuis hier",
    "userId": 1,
    "statut": "RECUE",
    "createdAt": "2026-01-20T22:46:50.231828711",
    "updatedAt": "2026-01-20T22:46:50.231828711"
  }
]
```

### 6. Update Reclamation Status
**PUT** `/api/reclamations/{id}/statut`

**Request Body:**
```json
{
  "statut": "EN_COURS"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "titre": "Problème de connexion",
  "description": "Je ne peux pas me connecter à mon compte depuis hier",
  "userId": 1,
  "statut": "EN_COURS",
  "createdAt": "2026-01-20T22:46:50.231828711",
  "updatedAt": "2026-01-20T22:47:15.123456789"
}
```

### 7. Take Charge of Reclamation
**PATCH** `/api/reclamations/{id}/prendre-en-charge`

Transitions status from `RECUE` to `EN_COURS`.

**Response:** `200 OK`
```json
{
  "id": 1,
  "titre": "Problème de connexion",
  "description": "Je ne peux pas me connecter à mon compte depuis hier",
  "userId": 1,
  "statut": "EN_COURS",
  "createdAt": "2026-01-20T22:46:50.231828711",
  "updatedAt": "2026-01-20T22:47:15.123456789"
}
```

**Error:** `400 Bad Request` - If status is not `RECUE`

### 8. Treat Reclamation
**PATCH** `/api/reclamations/{id}/traiter`

Transitions status to `TRAITEE`.

**Response:** `200 OK`
```json
{
  "id": 1,
  "titre": "Problème de connexion",
  "description": "Je ne peux pas me connecter à mon compte depuis hier",
  "userId": 1,
  "statut": "TRAITEE",
  "createdAt": "2026-01-20T22:46:50.231828711",
  "updatedAt": "2026-01-20T22:48:30.987654321"
}
```

**Error:** `400 Bad Request` - If already `TRAITEE`

## Service Layer

### ReclamationService
Main service class handling business logic:
- **createReclamation()**: Creates a new reclamation with user validation
- **getAllReclamations()**: Retrieves all reclamations
- **getReclamationById()**: Retrieves reclamation by ID
- **getReclamationsByUserId()**: Retrieves all reclamations for a user
- **getReclamationsByStatut()**: Retrieves reclamations by status
- **updateStatut()**: Updates reclamation status
- **prendreEnCharge()**: Transitions from RECUE to EN_COURS
- **traiterReclamation()**: Transitions to TRAITEE

### UserServiceClient
Service client for inter-service communication:
- Uses WebClient to communicate with User Service
- **userExists()**: Verifies user existence before creating reclamations
- Uses Eureka service discovery to locate User Service

## Repository Layer

### ReclamationRepository
Extends `JpaRepository<Reclamation, Long>` with custom methods:
- `findByUserId(Long userId)`: Find all reclamations for a user
- `findByStatut(StatutReclamation statut)`: Find reclamations by status
- `findByUserIdAndStatut(Long userId, StatutReclamation statut)`: Find by user and status

## Inter-Service Communication

### WebClient Configuration
The service uses a `@LoadBalanced` WebClient to communicate with User Service:
- Service name: `user-service`
- Endpoint: `/api/users/{id}/exists`
- Uses Eureka for service discovery

## Running the Service

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL running on localhost:3306
- Database `reclamationdb` (auto-created if configured)
- User Service must be running and registered with Eureka

### Start Command
```bash
cd reclamationservice
mvn spring-boot:run
```

### Database Setup
```sql
CREATE DATABASE reclamationdb;
```

Or the service will attempt to create it automatically if MySQL allows.

## Integration with Other Services

### Depends On
- **User Service**: Validates user existence before creating reclamations
- **Eureka Server**: For service discovery

### Used By
- **API Gateway**: Routes reclamation-related requests to this service

## Status Workflow

```
RECUE → EN_COURS → TRAITEE
  ↓         ↓
(Initial) (Taken) (Resolved)
```

1. **RECUE**: Reclamation is created and received
2. **EN_COURS**: Reclamation is being processed (via `prendre-en-charge`)
3. **TRAITEE**: Reclamation is resolved (via `traiter`)

## Example Usage

### Create a Reclamation
```bash
curl -X POST http://localhost:8082/api/reclamations \
  -H "Content-Type: application/json" \
  -d '{
    "titre": "Problème de connexion",
    "description": "Je ne peux pas me connecter à mon compte depuis hier",
    "userId": 1
  }'
```

### Get All Reclamations
```bash
curl http://localhost:8082/api/reclamations
```

### Take Charge of Reclamation
```bash
curl -X PATCH http://localhost:8082/api/reclamations/1/prendre-en-charge
```

### Treat Reclamation
```bash
curl -X PATCH http://localhost:8082/api/reclamations/1/traiter
```

### Get Reclamations by Status
```bash
curl http://localhost:8082/api/reclamations/statut/EN_COURS
```

## Error Handling

- **400 Bad Request**: Invalid input, user doesn't exist, or invalid status transition
- **404 Not Found**: Reclamation not found
- **500 Internal Server Error**: Database errors or service communication failures

## Logging

The service uses SLF4J with Lombok's `@Slf4j` annotation for logging. All operations are logged at INFO level, errors at ERROR level, including inter-service communication.
