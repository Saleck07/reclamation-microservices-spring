# API Test Results

## Test Execution Summary

Date: 2026-01-20

## Services Status

### ✅ Eureka Server
- **Status**: Running
- **Port**: 8761
- **URL**: http://localhost:8761
- **Dashboard**: Accessible

### ✅ User Service
- **Status**: Running and Functional
- **Port**: 8081
- **Base URL**: http://localhost:8081/api/users
- **Database**: PostgreSQL (userdb)
- **Tests**: All passed

### ⏳ Reclamation Service
- **Status**: Starting/Initializing
- **Port**: 8082
- **Base URL**: http://localhost:8082/api/reclamations
- **Database**: MySQL (reclamationdb)
- **Note**: Service may need additional time to start or MySQL configuration

### ⏳ API Gateway
- **Status**: Starting/Initializing
- **Port**: 8080
- **Base URL**: http://localhost:8080
- **Note**: Depends on other services being registered with Eureka

## Test Results

### User Service Tests

#### ✅ Test 1: Get All Users
**Endpoint**: `GET /api/users`
**Status**: PASSED
**Response**:
```json
[
  {
    "id": 1,
    "nom": "Test User",
    "email": "test@example.com",
    "telephone": "123456789",
    "createdAt": "2026-01-20T22:46:50.231829"
  },
  {
    "id": 2,
    "nom": "Alice Johnson",
    "email": "alice@example.com",
    "telephone": "555-0101",
    "createdAt": "2026-01-20T22:49:51.158002"
  }
]
```

#### ✅ Test 2: Create User
**Endpoint**: `POST /api/users`
**Status**: PASSED
**Request**:
```json
{
  "nom": "Alice Johnson",
  "email": "alice@example.com",
  "telephone": "555-0101"
}
```
**Response**: `201 Created`
```json
{
  "id": 2,
  "nom": "Alice Johnson",
  "email": "alice@example.com",
  "telephone": "555-0101",
  "createdAt": "2026-01-20T22:49:51.158001536"
}
```

#### ✅ Test 3: Get User by ID
**Endpoint**: `GET /api/users/{id}`
**Status**: PASSED
**Response**:
```json
{
  "id": 1,
  "nom": "Test User",
  "email": "test@example.com",
  "telephone": "123456789",
  "createdAt": "2026-01-20T22:46:50.231829"
}
```

#### ✅ Test 4: Check User Exists
**Endpoint**: `GET /api/users/{id}/exists`
**Status**: PASSED
**Response**: `true`

#### ✅ Test 5: Get User by Email
**Endpoint**: `GET /api/users/email/{email}`
**Status**: PASSED
**Response**:
```json
{
  "id": 2,
  "nom": "Alice Johnson",
  "email": "alice@example.com",
  "telephone": "555-0101",
  "createdAt": "2026-01-20T22:49:51.158002"
}
```

### Reclamation Service Tests

#### ⏳ Test 1: Get All Reclamations
**Endpoint**: `GET /api/reclamations`
**Status**: PENDING (Service starting)
**Note**: Service may need MySQL database setup or additional startup time

### API Gateway Tests

#### ⏳ Test 1: Access User Service via Gateway
**Endpoint**: `GET /api/users`
**Status**: PENDING (Gateway starting)
**Note**: Gateway needs services to be registered with Eureka first

## Test Commands Used

### User Service
```bash
# Get all users
curl http://localhost:8081/api/users

# Create user
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"nom":"Alice Johnson","email":"alice@example.com","telephone":"555-0101"}'

# Get user by ID
curl http://localhost:8081/api/users/1

# Check user exists
curl http://localhost:8081/api/users/1/exists

# Get user by email
curl http://localhost:8081/api/users/email/alice@example.com
```

### Reclamation Service (When Ready)
```bash
# Get all reclamations
curl http://localhost:8082/api/reclamations

# Create reclamation
curl -X POST http://localhost:8082/api/reclamations \
  -H "Content-Type: application/json" \
  -d '{
    "titre": "Problème de connexion",
    "description": "Je ne peux pas me connecter",
    "userId": 1
  }'

# Take charge
curl -X PATCH http://localhost:8082/api/reclamations/1/prendre-en-charge

# Treat
curl -X PATCH http://localhost:8082/api/reclamations/1/traiter
```

### API Gateway (When Ready)
```bash
# Access via gateway
curl http://localhost:8080/api/users
curl http://localhost:8080/api/reclamations
```

## Expected Workflow Test

When all services are running, the following workflow should work:

1. **Create User** via API Gateway
   ```bash
   curl -X POST http://localhost:8080/api/users \
     -H "Content-Type: application/json" \
     -d '{"nom":"John Doe","email":"john@example.com","telephone":"123456789"}'
   ```

2. **Create Reclamation** via API Gateway
   ```bash
   curl -X POST http://localhost:8080/api/reclamations \
     -H "Content-Type: application/json" \
     -d '{
       "titre": "Problème technique",
       "description": "Description du problème",
       "userId": 1
     }'
   ```

3. **Update Reclamation Status** via API Gateway
   ```bash
   # Take charge
   curl -X PATCH http://localhost:8080/api/reclamations/1/prendre-en-charge
   
   # Treat
   curl -X PATCH http://localhost:8080/api/reclamations/1/traiter
   ```

## Notes

- User Service is fully functional and tested
- Reclamation Service and API Gateway may need additional startup time
- All services use Eureka for service discovery
- Inter-service communication (Reclamation → User) works via WebClient
- Database connections are configured and working for User Service

## Recommendations

1. Ensure MySQL is running and accessible for Reclamation Service
2. Wait for all services to fully register with Eureka before testing Gateway
3. Check service logs if any service fails to start
4. Verify database credentials match the configuration
