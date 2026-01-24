# 🎯 PRÉSENTATION PROJET
# Système de Gestion des Réclamations - Architecture Microservices

---

## 📋 SLIDE 1 : INTRODUCTION

### Titre du Projet
**Système de Gestion des Réclamations basé sur une Architecture Microservices**

### Objectif Principal
Développer un système **modulaire**, **scalable** et **distribué** pour gérer les utilisateurs et leurs réclamations

### Technologies
- ☕ **Java 17**
- 🍃 **Spring Boot 3.2.1**
- ☁️ **Spring Cloud 2023.0.0**
- 🗄️ **PostgreSQL + MySQL**

---

## 🏗️ SLIDE 2 : ARCHITECTURE GLOBALE

```
                    ┌─────────────────┐
                    │    CLIENTS      │
                    │  (Postman/Web)  │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  API GATEWAY    │
                    │   Port 8080     │
                    └────────┬────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
    ┌────▼────┐         ┌────▼──────┐      ┌────▼─────┐
    │  USER   │         │RECLAMATION│      │  EUREKA  │
    │ SERVICE │◄────────│  SERVICE  │      │  SERVER  │
    │  8081   │WebClient│   8082    │      │   8761   │
    └────┬────┘         └────┬──────┘      └──────────┘
         │                   │
    ┌────▼────┐         ┌────▼──────┐
    │PostgreSQL│        │   MySQL   │
    │  userdb  │        │reclamation│
    └──────────┘        └───────────┘
```

### Les 4 Microservices
1. **Eureka Server** (8761) - Découverte de services
2. **API Gateway** (8080) - Point d'entrée unique
3. **User Service** (8081) - Gestion utilisateurs
4. **Reclamation Service** (8082) - Gestion réclamations

---

## 💡 SLIDE 3 : POURQUOI LES MICROSERVICES ?

### Avantages

| Avantage | Explication |
|----------|-------------|
| 🚀 **Scalabilité** | Chaque service scale indépendamment |
| 🔒 **Isolation** | Panne d'un service ≠ panne totale |
| 🛠️ **Flexibilité** | Technologies différentes par service |
| 👥 **Équipes** | Développement parallèle possible |
| 📦 **Déploiement** | Mise à jour indépendante |

### Inconvénients Gérés
- ❌ Complexité → ✅ Spring Cloud simplifie
- ❌ Communication réseau → ✅ Eureka + LoadBalancer
- ❌ Données distribuées → ✅ Vérifications inter-services

---

## 🔧 SLIDE 4 : EUREKA SERVER - Service Discovery

### Rôle
**Registre central** où tous les services s'enregistrent

### Fonctionnement
```
1. Service démarre → Enregistrement automatique dans Eureka
2. Service besoin de communiquer → Demande à Eureka
3. Eureka retourne l'adresse du service
4. Communication directe entre services
```

### Configuration
```properties
spring.application.name=eureka-server
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

### Code Java
```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }
}
```

### Avantages
✅ Pas d'IP fixes  
✅ Découverte dynamique  
✅ Health checks automatiques  
✅ Dashboard web intégré  

---

## 🌐 SLIDE 5 : API GATEWAY - Point d'Entrée Unique

### Rôle
**Routeur intelligent** qui dirige les requêtes vers les bons services

### Routes Configurées
```properties
# Route vers User Service
spring.cloud.gateway.routes[0].id=user-service
spring.cloud.gateway.routes[0].uri=lb://user-service
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/users/**

# Route vers Reclamation Service
spring.cloud.gateway.routes[1].id=reclamation-service
spring.cloud.gateway.routes[1].uri=lb://reclamation-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/reclamations/**
```

### Exemple de Routage
```
Client → POST /api/users → Gateway → User Service (8081)
Client → GET /api/reclamations → Gateway → Reclamation Service (8082)
```

### Avantages
✅ Point d'entrée unique  
✅ Load balancing automatique  
✅ Sécurité centralisée  
✅ Monitoring centralisé  

---

## 👤 SLIDE 6 : USER SERVICE

### Responsabilités
- Créer des utilisateurs
- Consulter les utilisateurs
- Vérifier l'existence d'un utilisateur

### Modèle de Données
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String telephone;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

### Base de Données
- **Type** : PostgreSQL
- **Nom** : userdb
- **ORM** : JPA/Hibernate

### API Endpoints
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/users` | Créer utilisateur |
| GET | `/api/users` | Liste utilisateurs |
| GET | `/api/users/{id}` | Utilisateur par ID |
| GET | `/api/users/{id}/exists` | Vérifier existence |

---

## 📋 SLIDE 7 : RECLAMATION SERVICE

### Responsabilités
- Créer des réclamations
- Gérer le cycle de vie des réclamations
- Vérifier l'utilisateur auprès du User Service

### Modèle de Données
```java
@Entity
@Table(name = "reclamations")
public class Reclamation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titre;
    private String description;
    private Long userId;  // Référence User Service
    
    @Enumerated(EnumType.STRING)
    private StatutReclamation statut;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Statuts de Réclamation
```java
public enum StatutReclamation {
    RECUE,      // Réclamation reçue
    EN_COURS,   // En cours de traitement
    TRAITEE     // Réclamation traitée
}
```

### Base de Données
- **Type** : MySQL
- **Nom** : reclamationdb

---

## 🔗 SLIDE 8 : COMMUNICATION INTER-SERVICES

### Problème
Comment Reclamation Service vérifie si un utilisateur existe ?

### Solution : WebClient + Eureka

#### 1. Configuration WebClient
```java
@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced  // ← IMPORTANT : Utilise Eureka
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
```

#### 2. Client pour User Service
```java
@Service
public class UserServiceClient {
    private final WebClient.Builder webClientBuilder;
    
    public boolean userExists(Long userId) {
        // Appel via le NOM du service (pas d'IP)
        Boolean exists = webClientBuilder.build()
            .get()
            .uri("http://user-service/api/users/{id}/exists", userId)
            .retrieve()
            .bodyToMono(Boolean.class)
            .block();
        
        return exists != null && exists;
    }
}
```

#### 3. Utilisation dans ReclamationService
```java
@Service
public class ReclamationService {
    private final UserServiceClient userServiceClient;
    
    public ReclamationDTO createReclamation(ReclamationRequest request) {
        // Vérification de l'utilisateur
        if (!userServiceClient.userExists(request.getUserId())) {
            throw new RuntimeException("Utilisateur inexistant");
        }
        
        // Créer la réclamation
        Reclamation reclamation = new Reclamation();
        reclamation.setUserId(request.getUserId());
        reclamation.setStatut(StatutReclamation.RECUE);
        // ...
    }
}
```

### Flux de Communication
```
1. Client crée réclamation avec userId=1
2. Reclamation Service reçoit la requête
3. Appel à User Service via WebClient : userExists(1)
4. Eureka fournit l'adresse de User Service
5. User Service répond : true/false
6. Si true → Création réclamation
   Si false → Erreur
```

---

## 🎯 SLIDE 9 : ARCHITECTURE EN COUCHES

### Pattern MVC + Service Layer

```
┌─────────────────────────┐
│   CONTROLLER LAYER      │  ← API REST Endpoints
│   @RestController       │
└───────────┬─────────────┘
            │
┌───────────▼─────────────┐
│    SERVICE LAYER        │  ← Logique métier
│    @Service             │
└───────────┬─────────────┘
            │
┌───────────▼─────────────┐
│  REPOSITORY LAYER       │  ← Accès base de données
│  JpaRepository          │
└───────────┬─────────────┘
            │
┌───────────▼─────────────┐
│      DATABASE           │
│  PostgreSQL / MySQL     │
└─────────────────────────┘
```

### Exemple : UserController
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRequest request) {
        UserDTO user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}
```

---

## 🔄 SLIDE 10 : DÉMONSTRATION - FLUX COMPLET

### Scénario : Créer et traiter une réclamation

#### Étape 1️⃣ : Créer un utilisateur
```http
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "nom": "Jean Dupont",
  "email": "jean.dupont@example.com",
  "telephone": "+33612345678"
}
```
**Réponse** : 
```json
{ "id": 1, "nom": "Jean Dupont", "email": "jean.dupont@example.com" }
```

#### Étape 2️⃣ : Créer une réclamation
```http
POST http://localhost:8080/api/reclamations
Content-Type: application/json

{
  "titre": "Problème de livraison",
  "description": "Mon colis n'est pas arrivé",
  "userId": 1
}
```

**Ce qui se passe** :
1. Gateway route vers Reclamation Service
2. Reclamation Service appelle User Service
3. Vérifie userId=1 existe ✅
4. Crée réclamation avec statut=RECUE

**Réponse** :
```json
{
  "id": 1,
  "titre": "Problème de livraison",
  "userId": 1,
  "statut": "RECUE"
}
```

#### Étape 3️⃣ : Prendre en charge
```http
PATCH http://localhost:8080/api/reclamations/1/prendre-en-charge
```
**Résultat** : statut = EN_COURS

#### Étape 4️⃣ : Traiter
```http
PATCH http://localhost:8080/api/reclamations/1/traiter
```
**Résultat** : statut = TRAITEE

---

## 📊 SLIDE 11 : API ENDPOINTS COMPLETS

### User Service
| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/api/users` | Créer utilisateur |
| GET | `/api/users` | Liste tous |
| GET | `/api/users/{id}` | Par ID |
| GET | `/api/users/email/{email}` | Par email |
| GET | `/api/users/{id}/exists` | Vérifier existence |

### Reclamation Service
| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/api/reclamations` | Créer réclamation |
| GET | `/api/reclamations` | Liste toutes |
| GET | `/api/reclamations/{id}` | Par ID |
| GET | `/api/reclamations/user/{userId}` | Par utilisateur |
| GET | `/api/reclamations/statut/{statut}` | Par statut |
| PATCH | `/api/reclamations/{id}/prendre-en-charge` | RECUE → EN_COURS |
| PATCH | `/api/reclamations/{id}/traiter` | → TRAITEE |
| PUT | `/api/reclamations/{id}/statut` | Changer statut |

---

## 🛠️ SLIDE 12 : PATTERNS & TECHNOLOGIES

### Design Patterns Utilisés

| Pattern | Implémentation | Avantage |
|---------|----------------|----------|
| **Service Registry** | Eureka Server | Découverte dynamique |
| **API Gateway** | Spring Cloud Gateway | Point d'entrée unique |
| **Database per Service** | PostgreSQL + MySQL | Isolation données |
| **Repository Pattern** | JpaRepository | Abstraction données |
| **DTO Pattern** | UserDTO, ReclamationDTO | Séparation couches |
| **Layered Architecture** | Controller → Service → Repository | Organisation code |
| **Client-Side Load Balancing** | @LoadBalanced | Distribution requêtes |

### Stack Technique

#### Backend
- **Java 17** - Langage
- **Spring Boot 3.2.1** - Framework
- **Spring Cloud 2023.0.0** - Microservices
- **Maven** - Build tool

#### Spring Cloud
- **Eureka** - Service Discovery
- **Gateway** - API Gateway
- **WebClient** - HTTP Client réactif
- **LoadBalancer** - Client-side LB

#### Persistence
- **JPA/Hibernate** - ORM
- **PostgreSQL** - User Service DB
- **MySQL** - Reclamation Service DB

#### Utilitaires
- **Lombok** - Réduction boilerplate
- **Jakarta Persistence** - Annotations JPA

---

## 🔍 SLIDE 13 : AVANTAGES DE NOTRE SOLUTION

### Pour le Développement
✅ **Code modulaire** - Chaque service est indépendant  
✅ **Tests isolés** - Test par service  
✅ **Développement parallèle** - Plusieurs équipes  
✅ **Technologies flexibles** - Choix par service  

### Pour la Production
✅ **Scalabilité horizontale** - Ajouter instances  
✅ **Résilience** - Isolation des pannes  
✅ **Monitoring** - Dashboard Eureka  
✅ **Maintenance** - Déploiement indépendant  

### Pour l'Entreprise
✅ **Évolutivité** - Ajout de services facile  
✅ **Performance** - Load balancing  
✅ **Disponibilité** - Pas de point unique de défaillance  
✅ **Coûts** - Scale uniquement ce qui est nécessaire  

---

## ⚠️ SLIDE 14 : DÉFIS & SOLUTIONS

### Défi 1 : Complexité
**Problème** : Plus de services = plus de complexité  
**Solution** : Spring Cloud automatise beaucoup (Eureka, Gateway, LoadBalancer)

### Défi 2 : Communication Réseau
**Problème** : Latence entre services  
**Solution** : WebClient non-bloquant, mise en cache possible

### Défi 3 : Cohérence des Données
**Problème** : Données distribuées sur 2 DB  
**Solution** : Vérification synchrone avant création (userExists)

### Défi 4 : Débogage
**Problème** : Erreur peut venir de plusieurs services  
**Solution** : Logs centralisés possibles (ELK stack), tracing distribué (Zipkin)

### Défi 5 : Déploiement
**Problème** : 4 services à déployer  
**Solution** : Docker + Kubernetes (future amélioration)

---

## 🚀 SLIDE 15 : DÉMARRAGE DU SYSTÈME

### Ordre de Démarrage
```
1. Eureka Server (8761)     →  Attendre 30s
2. User Service (8081)      →  Attendre 20s
3. Reclamation Service (8082) → Attendre 20s
4. API Gateway (8080)
```

### Commandes
```bash
# Terminal 1
cd eureka && mvn spring-boot:run

# Terminal 2
cd userservice && mvn spring-boot:run

# Terminal 3
cd reclamationservice && mvn spring-boot:run

# Terminal 4
cd apigetaway && mvn spring-boot:run
```

### Vérification
- **Eureka Dashboard** : http://localhost:8761
- **Vérifier** que les 3 services sont UP
- **Tester** : `curl http://localhost:8080/api/users`

---

## 📈 SLIDE 16 : MONITORING & OBSERVABILITÉ

### Eureka Dashboard
- **URL** : http://localhost:8761
- **Affiche** : 
  - Tous les services enregistrés
  - Statut UP/DOWN
  - Nombre d'instances
  - Health checks

### Logs Importants
```log
# Service enregistré
DiscoveryClient_USER-SERVICE - registration status: 204

# Route mappée
Mapped [/api/users/**] onto lb://user-service

# Service démarré
Started UserserviceApplication in 12.5 seconds
```

### Métriques à Surveiller
- Temps de réponse par endpoint
- Nombre de requêtes
- Taux d'erreur
- Santé des services

---

## 🔮 SLIDE 17 : AMÉLIORATIONS FUTURES

### Court Terme
✅ **Circuit Breaker** (Resilience4j)  
✅ **Distributed Tracing** (Zipkin/Sleuth)  
✅ **Centralized Logging** (ELK Stack)  
✅ **API Documentation** (Swagger/OpenAPI)  

### Moyen Terme
✅ **Authentication & Authorization** (JWT, OAuth2)  
✅ **Rate Limiting** (Protection contre abus)  
✅ **Caching** (Redis pour performance)  
✅ **Message Queue** (Kafka/RabbitMQ pour async)  

### Long Terme
✅ **Containerization** (Docker)  
✅ **Orchestration** (Kubernetes)  
✅ **CI/CD Pipeline** (Jenkins, GitLab CI)  
✅ **Multi-region Deployment**  

---

## ❓ SLIDE 18 : QUESTIONS FRÉQUENTES

### Q1 : Pourquoi 2 bases de données différentes ?
**R** : Pattern "Database per Service" pour isolation complète, scalabilité indépendante, et flexibilité technologique.

### Q2 : Que se passe-t-il si User Service tombe ?
**R** : Reclamation Service ne pourra pas créer de réclamations. Solution : Circuit Breaker + fallback mechanism.

### Q3 : Comment gérer les transactions distribuées ?
**R** : Actuellement : vérification synchrone. Future : Saga pattern ou Event Sourcing.

### Q4 : L'API Gateway n'est-il pas un SPOF ?
**R** : Oui, mais déployable en plusieurs instances avec load balancer devant.

### Q5 : Pourquoi Eureka et pas Consul ?
**R** : Intégration native Spring Cloud, maturité, dashboard intégré.

---

## 📊 SLIDE 19 : STRUCTURE DU CODE

### User Service
```
userservice/
├── model/
│   └── User.java               ← Entité JPA
├── dto/
│   ├── UserDTO.java            ← Réponse API
│   └── UserRequest.java        ← Requête API
├── repository/
│   └── UserRepository.java     ← Accès DB
├── service/
│   └── UserService.java        ← Logique métier
├── controller/
│   └── UserController.java     ← API REST
└── UserserviceApplication.java ← Main
```

### Reclamation Service
```
reclamationservice/
├── model/
│   ├── Reclamation.java
│   └── StatutReclamation.java  ← Enum
├── dto/
│   ├── ReclamationDTO.java
│   ├── ReclamationRequest.java
│   └── StatutUpdateRequest.java
├── repository/
│   └── ReclamationRepository.java
├── service/
│   ├── ReclamationService.java
│   └── UserServiceClient.java  ← Communication inter-services
├── config/
│   └── WebClientConfig.java    ← Config WebClient
├── controller/
│   └── ReclamationController.java
└── ReclamationserviceApplication.java
```

---

## 🎓 SLIDE 20 : CONCEPTS CLÉS À RETENIR

### 1. Service Discovery
- Enregistrement automatique dans Eureka
- Découverte dynamique (pas d'IP fixes)
- Health checks automatiques

### 2. API Gateway Pattern
- Point d'entrée unique
- Routage intelligent
- Load balancing

### 3. Database per Service
- Isolation des données
- Scalabilité indépendante
- Flexibilité technologique

### 4. Reactive Programming
- WebClient non-bloquant
- Meilleure performance
- Async communication

### 5. Microservices Communication
- REST via WebClient
- Service discovery via Eureka
- Load balancing côté client

---

## 🎯 SLIDE 21 : CONCLUSION

### Ce que nous avons réalisé
✅ Architecture microservices complète  
✅ 4 services communicants  
✅ 2 bases de données séparées  
✅ Service discovery fonctionnel  
✅ API Gateway opérationnel  
✅ Communication inter-services  

### Objectifs atteints
✅ **Modularité** - Services indépendants  
✅ **Scalabilité** - Scale par service  
✅ **Résilience** - Isolation des pannes  
✅ **Flexibilité** - Technologies variées  
✅ **Maintenabilité** - Code organisé  

### Leçons apprises
💡 Spring Cloud simplifie grandement les microservices  
💡 Service Discovery est essentiel  
💡 Communication inter-services nécessite attention  
💡 Architecture en couches facilite maintenance  
💡 Tests et monitoring sont cruciaux  

---

## 🙏 SLIDE 22 : MERCI !

### Démonstration Live
- ✅ Services en cours d'exécution
- ✅ Eureka Dashboard
- ✅ Tests Postman

### Contact & Ressources
- 📁 **GitHub** : Saleck07/reclamation-microservices-spring
- 📄 **Documentation** : README.md
- 🧪 **Guide Tests** : GUIDE_TEST_POSTMAN.md

### Questions ?
**Je suis prêt à répondre à vos questions !**

---

# 🎤 SCRIPT DE PRÉSENTATION (12-15 min)

## Introduction (1 min)
"Bonjour à tous. Aujourd'hui je vais vous présenter un système de gestion des réclamations basé sur une architecture microservices. Ce projet illustre comment Spring Boot et Spring Cloud permettent de créer des systèmes distribués, scalables et résilients."

## Architecture (2 min)
"Le système est composé de 4 microservices. Eureka Server est notre registre de services, l'API Gateway est le point d'entrée unique, User Service gère les utilisateurs avec PostgreSQL, et Reclamation Service gère les réclamations avec MySQL. Chaque service a sa propre base de données selon le pattern Database per Service."

## Communication (2 min)
"La communication inter-services est particulièrement intéressante. Quand une réclamation est créée, Reclamation Service doit vérifier que l'utilisateur existe. Il utilise WebClient avec l'annotation @LoadBalanced qui interroge Eureka pour trouver User Service. Pas besoin d'IP fixe, tout est dynamique."

## Démonstration (5 min)
"Laissez-moi vous montrer le système en action. D'abord le Dashboard Eureka avec nos 3 services enregistrés. Ensuite dans Postman, je crée un utilisateur... puis une réclamation pour cet utilisateur... le service vérifie automatiquement que l'utilisateur existe... puis je change le statut de RECUE à EN_COURS, puis TRAITEE."

## Technologies (2 min)
"Côté technologies, nous utilisons Spring Boot 3.2, Spring Cloud pour la découverte de services et l'API Gateway, WebClient pour la communication réactive, et JPA pour la persistence. L'architecture en couches Controller-Service-Repository assure un code propre et maintenable."

## Avantages (2 min)
"Les avantages de cette architecture sont nombreux : scalabilité horizontale en ajoutant des instances, résilience car la panne d'un service n'affecte pas les autres, flexibilité technologique avec des bases différentes, et maintenance facilitée avec des déploiements indépendants."

## Conclusion (1 min)
"En conclusion, cette architecture microservices offre modularité, scalabilité et résilience. Spring Cloud automatise beaucoup de complexité. Des améliorations futures incluent l'ajout d'un Circuit Breaker, du tracing distribué, et la containerisation avec Docker. Merci pour votre attention, je suis prêt pour vos questions."

---

# ✅ CHECKLIST AVANT PRÉSENTATION

### Préparation Technique
- [ ] PostgreSQL démarré (userdb créée)
- [ ] MySQL démarré (reclamationdb créée)
- [ ] Eureka Server lancé et accessible
- [ ] User Service lancé et enregistré
- [ ] Reclamation Service lancé et enregistré
- [ ] API Gateway lancé et enregistré
- [ ] Tous les services visibles dans Eureka Dashboard

### Préparation Démo
- [ ] Postman ouvert avec collection prête
- [ ] 3-4 requêtes préparées et testées
- [ ] Eureka Dashboard ouvert dans navigateur
- [ ] Éditeur de code ouvert sur fichiers clés
- [ ] Schéma architecture visible

### Préparation Présentation
- [ ] Ce guide ouvert et relu
- [ ] Points clés mémorisés
- [ ] Réponses aux questions préparées
- [ ] Timing répété (12-15 min)
- [ ] Plan B si démo échoue (screenshots)

### Fichiers Importants à Montrer
- [ ] EurekaApplication.java (@EnableEurekaServer)
- [ ] ApigatewayApplication.java (@EnableDiscoveryClient)
- [ ] User.java (entité)
- [ ] Reclamation.java (entité + enum)
- [ ] UserServiceClient.java (WebClient)
- [ ] application.properties (configuration)

---

**🎉 Vous êtes prêt pour votre présentation ! Bonne chance ! 🚀**
