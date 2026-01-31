# Notification Service

Service de gestion des notifications par email pour le système de gestion des réclamations.

## Fonctionnalités

- Envoi de notifications par email lors de la création d'une réclamation
- Envoi de notifications lors de la prise en charge d'une réclamation
- Envoi de notifications lors du traitement d'une réclamation
- Historique des notifications envoyées
- Base de données H2 en mémoire pour le stockage des notifications

## Technologies

- Spring Boot 3.2.1
- Spring Data JPA
- Spring Mail
- H2 Database
- Netflix Eureka Client
- Lombok
- OpenAPI/Swagger

## Configuration

Le service fonctionne sur le port **8083**.

### Configuration Email

Pour activer l'envoi d'emails, configurez les propriétés suivantes dans `application.properties`:

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

## Endpoints API

- `POST /api/notifications` - Créer une notification
- `GET /api/notifications` - Récupérer toutes les notifications
- `GET /api/notifications/reclamation/{reclamationId}` - Récupérer les notifications d'une réclamation
- `GET /api/notifications/user/{userId}` - Récupérer les notifications d'un utilisateur

## Documentation API

Swagger UI disponible à: http://localhost:8083/swagger-ui.html

## H2 Console

Accéder à la console H2: http://localhost:8083/h2-console

- JDBC URL: `jdbc:h2:mem:notificationdb`
- Username: `sa`
- Password: (vide)

## Démarrage

```bash
mvn spring-boot:run
```
