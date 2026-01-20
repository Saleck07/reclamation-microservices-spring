# API Gateway Documentation

## Overview
API Gateway is the single entry point for all client requests. It routes requests to the appropriate microservices using service discovery, providing a unified API interface and load balancing capabilities.

## Technical Details

### Port
- **Port**: 8080
- **Base URL**: http://localhost:8080

### Technology Stack
- **Framework**: Spring Boot 4.0.1
- **Java Version**: 17
- **Spring Cloud Version**: 2025.1.0
- **Gateway**: Spring Cloud Gateway
- **Service Discovery**: Netflix Eureka Client

### Dependencies
- `spring-cloud-starter-gateway`
- `spring-cloud-starter-netflix-eureka-client`

## Configuration

### Application Properties
Located in: `apigetaway/src/main/resources/application.properties`

```properties
spring.application.name=api-gateway
server.port=8080

# Configuration Eureka Client
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true

# Configuration Spring Cloud Gateway - Routes
# Route vers User Service
spring.cloud.gateway.routes[0].id=user-service
spring.cloud.gateway.routes[0].uri=lb://user-service
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/users/**

# Route vers Reclamation Service
spring.cloud.gateway.routes[1].id=reclamation-service
spring.cloud.gateway.routes[1].uri=lb://reclamation-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/reclamations/**

# Découverte automatique des services
spring.cloud.gateway.discovery.locator.enabled=true
spring.cloud.gateway.discovery.locator.lower-case-service-id=true
```

### Routing Configuration

#### User Service Route
- **Route ID**: `user-service`
- **Path Pattern**: `/api/users/**`
- **Target Service**: `lb://user-service` (load-balanced)
- **Example**: `http://localhost:8080/api/users` → `http://user-service/api/users`

#### Reclamation Service Route
- **Route ID**: `reclamation-service`
- **Path Pattern**: `/api/reclamations/**`
- **Target Service**: `lb://reclamation-service` (load-balanced)
- **Example**: `http://localhost:8080/api/reclamations` → `http://reclamation-service/api/reclamations`

### Service Discovery
- **Enabled**: Yes
- **Eureka Server**: http://localhost:8761/eureka/
- **Auto Discovery**: Enabled with lower-case service IDs

## API Routes

### User Service Routes (via Gateway)
All requests to `/api/users/**` are routed to User Service:

- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/email/{email}` - Get user by email
- `GET /api/users/{id}/exists` - Check if user exists
- `POST /api/users` - Create a new user

### Reclamation Service Routes (via Gateway)
All requests to `/api/reclamations/**` are routed to Reclamation Service:

- `GET /api/reclamations` - Get all reclamations
- `GET /api/reclamations/{id}` - Get reclamation by ID
- `GET /api/reclamations/user/{userId}` - Get reclamations by user
- `GET /api/reclamations/statut/{statut}` - Get reclamations by status
- `POST /api/reclamations` - Create a new reclamation
- `PUT /api/reclamations/{id}/statut` - Update reclamation status
- `PATCH /api/reclamations/{id}/prendre-en-charge` - Take charge of reclamation
- `PATCH /api/reclamations/{id}/traiter` - Treat reclamation

## Load Balancing

The gateway uses Spring Cloud LoadBalancer (via `lb://` prefix) to:
- Distribute requests across multiple service instances
- Provide high availability
- Handle service instance failures gracefully

## Running the Service

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Eureka Server must be running
- Target services (User Service, Reclamation Service) should be registered with Eureka

### Start Command
```bash
cd apigetaway
mvn spring-boot:run
```

### Start Order
1. Start Eureka Server first
2. Start User Service and Reclamation Service
3. Start API Gateway last

## Architecture Role

API Gateway serves as:
- **Single Entry Point**: All client requests go through the gateway
- **Service Router**: Routes requests to appropriate microservices
- **Load Balancer**: Distributes load across service instances
- **Service Discovery Integration**: Uses Eureka to find services dynamically

## Example Usage

### Access User Service via Gateway
```bash
# Get all users
curl http://localhost:8080/api/users

# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "John Doe",
    "email": "john.doe@example.com",
    "telephone": "1234567890"
  }'

# Get user by ID
curl http://localhost:8080/api/users/1
```

### Access Reclamation Service via Gateway
```bash
# Get all reclamations
curl http://localhost:8080/api/reclamations

# Create a reclamation
curl -X POST http://localhost:8080/api/reclamations \
  -H "Content-Type: application/json" \
  -d '{
    "titre": "Problème de connexion",
    "description": "Je ne peux pas me connecter",
    "userId": 1
  }'

# Take charge of reclamation
curl -X PATCH http://localhost:8080/api/reclamations/1/prendre-en-charge
```

## Benefits

1. **Centralized Access**: Single point of entry for all services
2. **Service Abstraction**: Clients don't need to know service locations
3. **Load Balancing**: Automatic distribution of requests
4. **Service Discovery**: Dynamic routing based on Eureka registry
5. **Future Extensibility**: Easy to add authentication, rate limiting, etc.

## Error Handling

The gateway forwards errors from downstream services:
- **404 Not Found**: Service not found or route doesn't exist
- **503 Service Unavailable**: Service not registered with Eureka or unavailable
- **500 Internal Server Error**: Gateway or service errors

## Monitoring

- Check Eureka dashboard to see registered services
- Gateway logs show routing decisions
- Monitor service health through Eureka

## Future Enhancements

Potential additions:
- Authentication and Authorization (JWT, OAuth2)
- Rate Limiting
- Request/Response Transformation
- API Versioning
- Circuit Breaker (Resilience4j)
- Request Logging and Tracing
- CORS Configuration
