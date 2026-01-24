# Guide de Test du Système de Gestion des Réclamations - Postman

## 📋 Prérequis

### 1. Démarrer les bases de données

**PostgreSQL (User Service)**
```sql
-- Créer la base de données userdb
CREATE DATABASE userdb;
```

**MySQL (Reclamation Service)**
```sql
-- La base reclamationdb sera créée automatiquement
```

### 2. Ordre de démarrage des services

**Important** : Démarrer les services dans cet ordre :

1. **Eureka Server** (Port 8761)
   ```bash
   cd eureka
   ./mvnw spring-boot:run
   # ou
   mvn spring-boot:run
   ```

2. **User Service** (Port 8081)
   ```bash
   cd userservice
   ./mvnw spring-boot:run
   ```

3. **Reclamation Service** (Port 8082)
   ```bash
   cd reclamationservice
   ./mvnw spring-boot:run
   ```

4. **API Gateway** (Port 8080)
   ```bash
   cd apigetaway
   ./mvnw spring-boot:run
   ```

### 3. Vérification des services

- **Eureka Dashboard** : http://localhost:8761
  - Vérifiez que tous les services sont enregistrés (USER-SERVICE, RECLAMATION-SERVICE, API-GATEWAY)

---

## 🧪 Scénarios de Test sur Postman

### Collection Postman : Système de Réclamations

---

## 📁 1. USER SERVICE - Gestion des Utilisateurs

### 1.1. Créer un utilisateur

**Méthode** : `POST`  
**URL** : `http://localhost:8080/api/users`  
**Headers** :
```
Content-Type: application/json
```
**Body** (JSON) :
```json
{
  "nom": "Jean Dupont",
  "email": "jean.dupont@example.com",
  "telephone": "+33612345678"
}
```

**Réponse attendue** : `201 Created`
```json
{
  "id": 1,
  "nom": "Jean Dupont",
  "email": "jean.dupont@example.com",
  "telephone": "+33612345678",
  "createdAt": "2026-01-17T10:00:00"
}
```

---

### 1.2. Créer un deuxième utilisateur

**Méthode** : `POST`  
**URL** : `http://localhost:8080/api/users`  
**Body** (JSON) :
```json
{
  "nom": "Marie Martin",
  "email": "marie.martin@example.com",
  "telephone": "+33687654321"
}
```

---

### 1.3. Récupérer tous les utilisateurs

**Méthode** : `GET`  
**URL** : `http://localhost:8080/api/users`

**Réponse attendue** : `200 OK`
```json
[
  {
    "id": 1,
    "nom": "Jean Dupont",
    "email": "jean.dupont@example.com",
    "telephone": "+33612345678",
    "createdAt": "2026-01-17T10:00:00"
  },
  {
    "id": 2,
    "nom": "Marie Martin",
    "email": "marie.martin@example.com",
    "telephone": "+33687654321",
    "createdAt": "2026-01-17T10:05:00"
  }
]
```

---

### 1.4. Récupérer un utilisateur par ID

**Méthode** : `GET`  
**URL** : `http://localhost:8080/api/users/1`

**Réponse attendue** : `200 OK`
```json
{
  "id": 1,
  "nom": "Jean Dupont",
  "email": "jean.dupont@example.com",
  "telephone": "+33612345678",
  "createdAt": "2026-01-17T10:00:00"
}
```

---

### 1.5. Récupérer un utilisateur par email

**Méthode** : `GET`  
**URL** : `http://localhost:8080/api/users/email/jean.dupont@example.com`

---

### 1.6. Vérifier si un utilisateur existe

**Méthode** : `GET`  
**URL** : `http://localhost:8080/api/users/1/exists`

**Réponse attendue** : `200 OK`
```json
true
```

---

### 1.7. Mettre à jour un utilisateur

**Méthode** : `PUT`  
**URL** : `http://localhost:8080/api/users/1`  
**Headers** :
```
Content-Type: application/json
```
**Body** (JSON) :
```json
{
  "nom": "Jean Dupont Modifié",
  "email": "jean.dupont.nouveau@example.com",
  "telephone": "+33699887766"
}
```

**Réponse attendue** : `200 OK`
```json
{
  "id": 1,
  "nom": "Jean Dupont Modifié",
  "email": "jean.dupont.nouveau@example.com",
  "telephone": "+33699887766",
  "createdAt": "2026-01-17T10:00:00"
}
```

**Note** : Le système vérifie que le nouvel email n'est pas déjà utilisé par un autre utilisateur.

---

### 1.8. Supprimer un utilisateur

**Méthode** : `DELETE`  
**URL** : `http://localhost:8080/api/users/1`

**Réponse attendue** : `204 No Content`

**Note** : Attention, si des réclamations existent pour cet utilisateur, elles référenceront un utilisateur supprimé. Dans un système réel, il faudrait :
- Soit empêcher la suppression (vérifier les réclamations liées)
- Soit supprimer en cascade les réclamations
- Soit marquer l'utilisateur comme inactif au lieu de le supprimer

---

## 📁 2. RECLAMATION SERVICE - Gestion des Réclamations

### 2.1. Créer une réclamation (Utilisateur existant)

**Méthode** : `POST`  
**URL** : `http://localhost:8080/api/reclamations`  
**Headers** :
```
Content-Type: application/json
```
**Body** (JSON) :
```json
{
  "titre": "Problème de livraison",
  "description": "Mon colis n'est pas arrivé à la date prévue. Référence: CMD123456",
  "userId": 1
}
```

**Réponse attendue** : `201 Created`
```json
{
  "id": 1,
  "titre": "Problème de livraison",
  "description": "Mon colis n'est pas arrivé à la date prévue. Référence: CMD123456",
  "userId": 1,
  "statut": "RECUE",
  "createdAt": "2026-01-17T10:15:00",
  "updatedAt": "2026-01-17T10:15:00"
}
```

---

### 2.2. Créer une réclamation (Utilisateur inexistant - Erreur)

**Méthode** : `POST`  
**URL** : `http://localhost:8080/api/reclamations`  
**Body** (JSON) :
```json
{
  "titre": "Test réclamation",
  "description": "Description test",
  "userId": 999
}
```

**Réponse attendue** : `400 Bad Request`  
*Le système vérifie via User Service que l'utilisateur existe*

---

### 2.3. Créer plusieurs réclamations

**Réclamation 2** :
```json
{
  "titre": "Produit défectueux",
  "description": "L'article reçu présente un défaut de fabrication",
  "userId": 1
}
```

**Réclamation 3** :
```json
{
  "titre": "Mauvais article livré",
  "description": "J'ai reçu un article différent de ma commande",
  "userId": 2
}
```

---

### 2.4. Récupérer toutes les réclamations

**Méthode** : `GET`  
**URL** : `http://localhost:8080/api/reclamations`

**Réponse attendue** : `200 OK`
```json
[
  {
    "id": 1,
    "titre": "Problème de livraison",
    "description": "Mon colis n'est pas arrivé à la date prévue...",
    "userId": 1,
    "statut": "RECUE",
    "createdAt": "2026-01-17T10:15:00",
    "updatedAt": "2026-01-17T10:15:00"
  },
  {
    "id": 2,
    "titre": "Produit défectueux",
    "description": "L'article reçu présente un défaut...",
    "userId": 1,
    "statut": "RECUE",
    "createdAt": "2026-01-17T10:20:00",
    "updatedAt": "2026-01-17T10:20:00"
  }
]
```

---

### 2.5. Récupérer une réclamation par ID

**Méthode** : `GET`  
**URL** : `http://localhost:8080/api/reclamations/1`

---

### 2.6. Récupérer les réclamations d'un utilisateur

**Méthode** : `GET`  
**URL** : `http://localhost:8080/api/reclamations/user/1`

**Réponse** : Toutes les réclamations de l'utilisateur ID=1

---

### 2.7. Récupérer les réclamations par statut

**Méthode** : `GET`  
**URL** : `http://localhost:8080/api/reclamations/statut/RECUE`

**Statuts possibles** :
- `RECUE`
- `EN_COURS`
- `TRAITEE`

---

### 2.8. Mettre à jour une réclamation complète

**Méthode** : `PUT`  
**URL** : `http://localhost:8080/api/reclamations/1`  
**Headers** :
```
Content-Type: application/json
```
**Body** (JSON) :
```json
{
  "titre": "Problème de livraison - URGENT",
  "description": "Mon colis n'est toujours pas arrivé après 2 semaines. Référence: CMD123456. Merci de traiter en priorité.",
  "userId": 1
}
```

**Réponse attendue** : `200 OK`
```json
{
  "id": 1,
  "titre": "Problème de livraison - URGENT",
  "description": "Mon colis n'est toujours pas arrivé après 2 semaines. Référence: CMD123456. Merci de traiter en priorité.",
  "userId": 1,
  "statut": "RECUE",
  "createdAt": "2026-01-17T10:15:00",
  "updatedAt": "2026-01-17T11:00:00"
}
```

**Note** : 
- Le statut n'est **pas modifié** par cette méthode (utiliser `/statut` pour ça)
- Si vous changez le `userId`, le système vérifie que le nouvel utilisateur existe
- Les dates `createdAt` restent inchangées, seul `updatedAt` est mis à jour

---

## 📁 3. GESTION DU CYCLE DE VIE DES RÉCLAMATIONS

### 3.1. Prendre en charge une réclamation (RECUE → EN_COURS)

**Méthode** : `PATCH`  
**URL** : `http://localhost:8080/api/reclamations/1/prendre-en-charge`

**Réponse attendue** : `200 OK`
```json
{
  "id": 1,
  "titre": "Problème de livraison",
  "description": "Mon colis n'est pas arrivé à la date prévue...",
  "userId": 1,
  "statut": "EN_COURS",
  "createdAt": "2026-01-17T10:15:00",
  "updatedAt": "2026-01-17T10:30:00"
}
```

---

### 3.2. Vérifier le changement de statut

**Méthode** : `GET`  
**URL** : `http://localhost:8080/api/reclamations/statut/EN_COURS`

**Résultat** : Doit afficher la réclamation ID=1 avec statut EN_COURS

---

### 3.3. Traiter une réclamation (→ TRAITEE)

**Méthode** : `PATCH`  
**URL** : `http://localhost:8080/api/reclamations/1/traiter`

**Réponse attendue** : `200 OK`
```json
{
  "id": 1,
  "titre": "Problème de livraison",
  "description": "Mon colis n'est pas arrivé à la date prévue...",
  "userId": 1,
  "statut": "TRAITEE",
  "createdAt": "2026-01-17T10:15:00",
  "updatedAt": "2026-01-17T10:45:00"
}
```

---

### 3.4. Mettre à jour manuellement le statut

**Méthode** : `PUT`  
**URL** : `http://localhost:8080/api/reclamations/2/statut`  
**Headers** :
```
Content-Type: application/json
```
**Body** (JSON) :
```json
{
  "statut": "EN_COURS"
}
```

**Réponse attendue** : `200 OK` avec le statut mis à jour

---

### 3.5. Supprimer une réclamation

**Méthode** : `DELETE`  
**URL** : `http://localhost:8080/api/reclamations/1`

**Réponse attendue** : `204 No Content`

**Note** : Supprime définitivement la réclamation de la base de données.

---

## 🔍 Scénario de Test Complet

### Scénario : Cycle complet d'une réclamation

```
1. POST /api/users → Créer utilisateur (ID=1)
2. POST /api/reclamations → Créer réclamation pour user_id=1 (statut=RECUE)
3. GET /api/reclamations/user/1 → Vérifier les réclamations de l'utilisateur
4. PUT /api/reclamations/1 → Modifier le titre et description
5. PATCH /api/reclamations/1/prendre-en-charge → Passer en EN_COURS
6. GET /api/reclamations/statut/EN_COURS → Vérifier les réclamations en cours
7. PATCH /api/reclamations/1/traiter → Passer en TRAITEE
8. GET /api/reclamations/1 → Vérifier le statut final
9. DELETE /api/reclamations/1 → Supprimer la réclamation
10. PUT /api/users/1 → Modifier les infos utilisateur
11. DELETE /api/users/1 → Supprimer l'utilisateur
```

---

## 📊 Tests de Communication Inter-Services

### Test de vérification d'utilisateur

**Objectif** : Vérifier que Reclamation Service communique avec User Service

1. **Créer un utilisateur** :
   ```
   POST http://localhost:8080/api/users
   ```

2. **Créer une réclamation avec cet utilisateur** :
   ```
   POST http://localhost:8080/api/reclamations
   {
     "userId": 1,
     ...
   }
   ```
   ✅ Doit réussir (utilisateur existe)

3. **Créer une réclamation avec un utilisateur inexistant** :
   ```
   POST http://localhost:8080/api/reclamations
   {
     "userId": 999,
     ...
   }
   ```
   ❌ Doit échouer (utilisateur n'existe pas)

---

## 🌐 Accès Direct aux Services (Sans API Gateway)

Pour tester directement les services (bypass du Gateway) :

### User Service Direct
```
http://localhost:8081/api/users
```

### Reclamation Service Direct
```
http://localhost:8082/api/reclamations
```

### Eureka Dashboard
```
http://localhost:8761
```

---

## 🎯 Collection Postman Recommandée

### Structure de la collection

```
  ├─📁 1. User Service
  |   ├─ POST Créer utilisateur
  |   ├─ GET Tous les utilisateurs
  |   ├─ GET Utilisateur par ID
  |   ├─ GET Utilisateur par email
  |   ├─ GET Vérifier existence
  |   ├─ PUT Mettre à jour utilisateur
  |   └─ DELETE Supprimer utilisateur
  |
  ├─📁 2. Reclamation Service
  |   ├─ POST Créer réclamation
  |   ├─ GET Toutes les réclamations
  |   ├─ GET Réclamation par ID
  |   ├─ GET Réclamations par utilisateur
  |   ├─ GET Réclamations par statut
  |   ├─ PUT Mettre à jour réclamation complète
  |   └─ DELETE Supprimer réclamation
  |
  └─📁 3. Gestion des Statuts
      ├─ PATCH Prendre en charge
      ├─ PATCH Traiter  
      └─ PUT Mettre à jour statut
      ├─ PATCH Traiter  
      └─ PUT Mettre à jour statut
```

---

## 🐛 Dépannage

### Problème : 404 Not Found

- ✅ Vérifier que tous les services sont démarrés
- ✅ Vérifier Eureka Dashboard (http://localhost:8761)
- ✅ Attendre 30 secondes après le démarrage pour l'enregistrement

### Problème : 503 Service Unavailable

- ✅ Vérifier que le service cible est enregistré dans Eureka
- ✅ Vérifier les logs du service

### Problème : 400 Bad Request lors de la création de réclamation

- ✅ Vérifier que l'utilisateur existe (userId valide)
- ✅ Vérifier que User Service est accessible

---

## 📝 Variables d'Environnement Postman (Optionnel)

Créer un environnement avec ces variables :

```
gateway_url = http://localhost:8080
user_service_url = http://localhost:8081
reclamation_service_url = http://localhost:8082
eureka_url = http://localhost:8761
```

Utiliser dans les requêtes : `{{gateway_url}}/api/users`

---

## ✅ Checklist de Test

- [ ] Eureka Server accessible
- [ ] Tous les services enregistrés dans Eureka
- [ ] Créer un utilisateur via API Gateway
- [ ] Récupérer les utilisateurs
- [ ] Créer une réclamation avec utilisateur existant
- [ ] Tenter de créer réclamation avec utilisateur inexistant (doit échouer)
- [ ] Récupérer les réclamations
- [ ] Prendre en charge une réclamation (RECUE → EN_COURS)
- [ ] Traiter une réclamation (→ TRAITEE)
- [ ] Filtrer les réclamations par statut
- [ ] Filtrer les réclamations par utilisateur

---

**🎉 Bonne chance pour les tests !**
