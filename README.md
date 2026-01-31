# Reclamation Microservices System

A microservices-based system for managing users and reclamations (complaints/claims) with automated email notifications, built with Spring Boot and Spring Cloud.

## Architecture Overview

This system consists of five microservices:

1. **Eureka Server** - Service discovery server (Port 8761)
2. **User Service** - Manages user information (Port 8081)
3. **Reclamation Service** - Manages reclamations/complaints (Port 8082)
4. **Notification Service** - Sends email notifications (Port 8083) **NEW!**
5. **API Gateway** - Single entry point for all requests (Port 8080)

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
         ┌──────────────────────┼────────────────────────┐
         │                      │                        │
    ┌────▼─────┐          ┌─────▼──────┐         ┌──────▼──────┐
    │   User   │          │ Reclamation│         │Notification │
    │ Service  │◄─────────┤   Service  │────────►│   Service   │
    │(Port 8081)│          │ (Port 8082)│         │ (Port 8083) │
    └────┬─────┘          └─────┬──────┘         └──────┬──────┘
         │                      │                        │
    ┌────▼─────┐          ┌─────▼────────┐         ┌────▼──────┐
    │PostgreSQL│          │    MySQL     │         │H2 Database│
    │  userdb  │          │reclamationdb │         │  (Memory) │
    └──────────┘          └──────────────┘         └───────────┘
                                                          │
                    ┌─────────────────────────────────────┘
                    │
             ┌──────▼──────┐
             │ SMTP Server │
             │   (Gmail)   │
             └─────────────┘
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
- **Gmail Account** (for Email Notifications) **NEW!**
  - Gmail email address
  - App-specific password (see Email Configuration below)

## Email Configuration (Notification Service)

To enable email notifications, you need to configure Gmail SMTP:

1. **Enable 2-Step Verification** on your Gmail account
2. **Generate an App Password**:
   - Go to Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate a new app password for "Mail"
3. **Update** `notificationservice/src/main/resources/application.properties`:
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   ```

To disable email notifications (for testing):
```properties
notification.email.enabled=false
```

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

### 3. Start Notification Service **NEW!**
```bash
cd notificationservice
mvn spring-boot:run
```

### 4. Start Reclamation Service
```bash
cd reclamationservice
mvn spring-boot:run
```

### 5. Start API Gateway
```bash
cd apigetaway
mvn spring-boot:run
```

## Service URLs

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **User Service (Direct)**: http://localhost:8081
- **Reclamation Service (Direct)**: http://localhost:8082
- **Notification Service (Direct)**: http://localhost:8083
- **H2 Console (Notifications)**: http://localhost:8083/h2-console

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
# Create a reclamation (triggers email notification)
curl -X POST http://localhost:8080/api/reclamations \
  -H "Content-Type: application/json" \
  -d '{
    "titre": "Problème de connexion",
    "description": "Je ne peux pas me connecter à mon compte",
    "userId": 1
  }'

# Get all reclamations
curl http://localhost:8080/api/reclamations

# Take charge of reclamation (triggers "Prise en charge" email)
curl -X PATCH http://localhost:8080/api/reclamations/1/prendre-en-charge

# Treat reclamation (triggers "Traitée" email)
curl -X PATCH http://localhost:8080/api/reclamations/1/traiter
```

#### Notification Service **NEW!**
```bash
# Get all notifications
curl http://localhost:8080/api/notifications

# Get notifications for a reclamation
curl http://localhost:8080/api/notifications/reclamation/1

# Get notifications for a user
curl http://localhost:8080/api/notifications/user/1
```

## Documentation

Detailed documentation for each service:
- [Eureka Server Documentation](EUREKA_SERVER.md)
- [User Service Documentation](USER_SERVICE.md)
- [Reclamation Service Documentation](RECLAMATION_SERVICE.md)
- [**Notification Service Documentation**](NOTIFICATION_SERVICE.md) **NEW!**
- [API Gateway Documentation](API_GATEWAY.md)
- [**Testing Guide with Email Notifications**](GUIDE_TEST_NOTIFICATION.md) **NEW!**
- [**Summary of Notification Service Changes**](NOTIFICATION_SERVICE_CHANGES.md) **NEW!**

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
- Create, read, update, delete users
- Email uniqueness validation
- User existence checking (for other services)

### Reclamation Service
- Create, read, update, delete reclamations
- Status management (RECUE → EN_COURS → TRAITEE)
- User validation before creating reclamations
- Filter by user, status
- **Automatic notification triggers on status changes** **NEW!**

### Notification Service **NEW!**
- **Automated email notifications** for reclamation status changes
- HTML email templates with professional design
- Three notification types:
  - Réclamation reçue (RECUE)
  - Réclamation prise en charge (EN_COURS)
  - Réclamation traitée (TRAITEE)
- Email history tracking with status (SENT/FAILED/PENDING)
- Asynchronous email sending for better performance
- Filter notifications by reclamation or user
- H2 in-memory database for notification storage

### API Gateway
- Single entry point for all services
- Load balancing
- Service discovery integration
- Route management to all microservices

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

### H2 Database (Notification Service) **NEW!**
No setup required! The notification service uses an in-memory H2 database that is automatically created on startup.

Access H2 Console at: http://localhost:8083/h2-console
- JDBC URL: `jdbc:h2:mem:notificationdb`
- Username: `sa`
- Password: (leave empty)

## Service Communication Flow

1. **Client** → API Gateway
2. **API Gateway** → Eureka (Service Discovery)
3. **API Gateway** → Target Service (User/Reclamation/Notification)
4. **Reclamation Service** → User Service (User Validation & Info Retrieval)
5. **Reclamation Service** → Notification Service (Send Email) **NEW!**
6. **Notification Service** → User Service (Get User Email) **NEW!**
7. **Notification Service** → SMTP Server (Send Email) **NEW!**

## Email Notification Flow

```
User creates reclamation
        ↓
Reclamation Service
        ↓
Retrieves user info from User Service
        ↓
Sends notification request to Notification Service
        ↓
Notification Service generates HTML email
        ↓
Email sent via Gmail SMTP
        ↓
User receives email notification
```

### Example Email Scenarios

1. **New Reclamation Created** (Status: RECUE)
   - Email Subject: "Réclamation #1 reçue avec succès"
   - Content: Confirmation of receipt with reclamation details

2. **Reclamation Taken in Charge** (Status: EN_COURS)
   - Email Subject: "Votre réclamation #1 a été prise en charge"
   - Content: Notification that the team is working on it

3. **Reclamation Treated** (Status: TRAITEE)
   - Email Subject: "Votre réclamation #1 a été traitée"
   - Content: Confirmation of successful resolution

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
├── notificationservice/ # Notification Service (NEW!)
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
