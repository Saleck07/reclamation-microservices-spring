# Eureka Server Documentation

## Overview
Eureka Server is a service discovery server that enables microservices to register themselves and discover other services dynamically. It acts as the central registry for all microservices in the system.

## Technical Details

### Port
- **Port**: 8761
- **URL**: http://localhost:8761

### Technology Stack
- **Framework**: Spring Boot 4.0.1
- **Java Version**: 17
- **Spring Cloud Version**: 2025.1.0
- **Service Discovery**: Netflix Eureka Server

### Dependencies
- `spring-cloud-starter-netflix-eureka-server`

## Configuration

### Application Properties
Located in: `eureka/src/main/resources/application.properties`

```properties
spring.application.name=eureka-server
server.port=8761

# Configuration Eureka Server
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Console Eureka
eureka.server.enable-self-preservation=false
```

### Key Configuration Points
- **register-with-eureka=false**: Eureka server doesn't register itself
- **fetch-registry=false**: Eureka server doesn't fetch registry from other servers
- **enable-self-preservation=false**: Disables self-preservation mode for development

## Running the Service

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Start Command
```bash
cd eureka
mvn spring-boot:run
```

Or using the Maven wrapper:
```bash
cd eureka
./mvnw spring-boot:run
```

## Eureka Dashboard

Once the server is running, access the Eureka dashboard at:
- **URL**: http://localhost:8761

The dashboard displays:
- Registered services
- Service instances
- Service status
- Health information

## Registered Services

The following services register with Eureka:
1. **user-service** (Port 8081)
2. **reclamation-service** (Port 8082)
3. **api-gateway** (Port 8080)

## Architecture Role

Eureka Server is the foundation of the microservices architecture:
- Enables service discovery
- Allows services to find each other without hardcoded URLs
- Supports load balancing through service instances
- Provides health monitoring

## Health Checks

Eureka automatically performs health checks on registered services and removes unhealthy instances from the registry.

## Development Notes

- Self-preservation is disabled for easier development
- In production, consider enabling self-preservation
- For high availability, deploy multiple Eureka servers
