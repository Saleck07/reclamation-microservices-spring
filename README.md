# Reclamation Microservices System

A microservices-based system for managing users and reclamations (complaints/claims) built with Spring Boot and Spring Cloud.

## Architecture Overview

This system consists of four microservices:

1. **Eureka Server** - Service discovery server (Port 8761)
2. **User Service** - Manages user information (Port 8081)
3. **Reclamation Service** - Manages reclamations/complaints (Port 8082)
4. **API Gateway** - Single entry point for all requests (Port 8080)

## System Architecture

```
                    ┌─────────────┐
                    │   Clients   │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ API Gateway │
                    │  (Port 8080)│
                    └──────┬──────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                   │
   ┌────▼─────┐      ┌─────▼──────┐    ┌──────▼──────┐
   │   User   │      │ Reclamation│    │   Eureka    │
   │ Service  │      │   Service  │    │   Server    │
   │(Port 8081)│     │ (Port 8082)│    │ (Port 8761) │
   └────┬─────┘      └─────┬──────┘    └──────┬──────┘
        │                  │                   │
   ┌────▼─────┐      ┌─────▼────-──┐           │
   │PostgreSQL│      │    MySQL    │           │
   │  userdb  │      │reclamationdb│           │
   └──────────┘      └─────────────┘           │
                                                │
        ┌───────────────────────────────────────┘
        │
   Service Discovery
```

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **PostgreSQL** (for User Service)
  - Database: `userdb`
  - Username: `postgres`
  - Password: `babe`
- **MySQL** (for Reclamation Service)
  - Database: `reclamationdb`
  - Username: `root`
  - Password: (empty by default)

## Quick Start

### 1. Start Eureka Server
```bash
cd eureka
mvn spring-boot:run
```
Wait for Eureka to start (check http://localhost:8761)

### 2. Start User Service
```bash
cd userservice
mvn spring-boot:run
```

### 3. Start Reclamation Service
```bash
cd reclamationservice
mvn spring-boot:run
```

### 4. Start API Gateway
```bash
cd apigetaway
mvn spring-boot:run
```

## Service URLs

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **User Service (Direct)**: http://localhost:8081
- **Reclamation Service (Direct)**: http://localhost:8082

## API Testing

A test script is provided to test all endpoints:
```bash
./test-apis.sh
```

### Manual Testing Examples

#### User Service
```bash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "John Doe",
    "email": "john.doe@example.com",
    "telephone": "1234567890"
  }'

# Get all users
curl http://localhost:8080/api/users

# Get user by ID
curl http://localhost:8080/api/users/1
```

#### Reclamation Service
```bash
# Create a reclamation
curl -X POST http://localhost:8080/api/reclamations \
  -H "Content-Type: application/json" \
  -d '{
    "titre": "Problème de connexion",
    "description": "Je ne peux pas me connecter à mon compte",
    "userId": 1
  }'

# Get all reclamations
curl http://localhost:8080/api/reclamations

# Take charge of reclamation
curl -X PATCH http://localhost:8080/api/reclamations/1/prendre-en-charge

# Treat reclamation
curl -X PATCH http://localhost:8080/api/reclamations/1/traiter
```

## Documentation

Detailed documentation for each service:
- [Eureka Server Documentation](EUREKA_SERVER.md)
- [User Service Documentation](USER_SERVICE.md)
- [Reclamation Service Documentation](RECLAMATION_SERVICE.md)
- [API Gateway Documentation](API_GATEWAY.md)

## Technology Stack

- **Spring Boot 4.0.1**
- **Spring Cloud 2025.1.0**
- **Spring Cloud Gateway** (API Gateway)
- **Netflix Eureka** (Service Discovery)
- **Spring Data JPA** (Data Access)
- **PostgreSQL** (User Service Database)
- **MySQL** (Reclamation Service Database)
- **WebClient** (Inter-service Communication)
- **Lombok** (Code Generation)

## Features

### User Service
- Create, read users
- Email uniqueness validation
- User existence checking (for other services)

### Reclamation Service
- Create, read reclamations
- Status management (RECUE → EN_COURS → TRAITEE)
- User validation before creating reclamations
- Filter by user, status

### API Gateway
- Single entry point for all services
- Load balancing
- Service discovery integration
- Route management

### Eureka Server
- Service registration and discovery
- Health monitoring
- Service instance management

## Database Setup

### PostgreSQL (User Service)
```sql
CREATE DATABASE userdb;
```

The service will auto-create tables using JPA/Hibernate.

### MySQL (Reclamation Service)
```sql
CREATE DATABASE reclamationdb;
```

The service will auto-create tables using JPA/Hibernate.

## Service Communication Flow

1. **Client** → API Gateway
2. **API Gateway** → Eureka (Service Discovery)
3. **API Gateway** → Target Service (User/Reclamation)
4. **Reclamation Service** → User Service (User Validation)

## Development

### Project Structure
```
reclamation-microservices-spring/
├── eureka/              # Eureka Server
├── userservice/         # User Service
├── reclamationservice/  # Reclamation Service
├── apigetaway/          # API Gateway
└── test-apis.sh         # API Test Script
```

### Building
```bash
# Build all services
mvn clean install
```

### Running in Development
Use separate terminal windows for each service, or use a process manager like `tmux` or `screen`.

## Troubleshooting

### Services not starting
- Check if ports are available
- Verify database connections
- Ensure Eureka Server is running first
- Check service logs for errors

### Service not registered with Eureka
- Verify Eureka Server is running
- Check `eureka.client.service-url.defaultZone` configuration
- Wait a few seconds for registration

### Database connection errors
- Verify database is running
- Check credentials in `application.properties`
- Ensure database exists

## License

This project is for educational purposes.

## Author

Microservices System - Spring Boot & Spring Cloud
