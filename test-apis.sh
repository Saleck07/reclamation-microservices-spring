#!/bin/bash

echo "=========================================="
echo "API Testing Script for Microservices"
echo "=========================================="
echo ""

BASE_URL="http://localhost:8080"
USER_SERVICE_DIRECT="http://localhost:8081"
RECLAMATION_SERVICE_DIRECT="http://localhost:8082"

echo "1. Testing User Service (Direct)"
echo "-----------------------------------"
echo "1.1 Get all users (should be empty initially):"
curl -s "$USER_SERVICE_DIRECT/api/users" | jq . 2>/dev/null || curl -s "$USER_SERVICE_DIRECT/api/users"
echo ""
echo ""

echo "1.2 Create a new user:"
USER_RESPONSE=$(curl -s -X POST "$USER_SERVICE_DIRECT/api/users" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "John Doe",
    "email": "john.doe@example.com",
    "telephone": "1234567890"
  }')
echo "$USER_RESPONSE" | jq . 2>/dev/null || echo "$USER_RESPONSE"
USER_ID=$(echo "$USER_RESPONSE" | jq -r '.id' 2>/dev/null || echo "1")
echo ""
echo "User ID: $USER_ID"
echo ""

echo "1.3 Get user by ID:"
curl -s "$USER_SERVICE_DIRECT/api/users/$USER_ID" | jq . 2>/dev/null || curl -s "$USER_SERVICE_DIRECT/api/users/$USER_ID"
echo ""
echo ""

echo "1.4 Create another user:"
curl -s -X POST "$USER_SERVICE_DIRECT/api/users" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Jane Smith",
    "email": "jane.smith@example.com",
    "telephone": "0987654321"
  }' | jq . 2>/dev/null || echo "User created"
echo ""
echo ""

echo "1.5 Get all users:"
curl -s "$USER_SERVICE_DIRECT/api/users" | jq . 2>/dev/null || curl -s "$USER_SERVICE_DIRECT/api/users"
echo ""
echo ""

echo "2. Testing Reclamation Service (Direct)"
echo "-----------------------------------"
echo "2.1 Get all reclamations (should be empty initially):"
curl -s "$RECLAMATION_SERVICE_DIRECT/api/reclamations" | jq . 2>/dev/null || curl -s "$RECLAMATION_SERVICE_DIRECT/api/reclamations"
echo ""
echo ""

echo "2.2 Create a new reclamation:"
RECLAMATION_RESPONSE=$(curl -s -X POST "$RECLAMATION_SERVICE_DIRECT/api/reclamations" \
  -H "Content-Type: application/json" \
  -d "{
    \"titre\": \"Problème de connexion\",
    \"description\": \"Je ne peux pas me connecter à mon compte depuis hier\",
    \"userId\": $USER_ID
  }")
echo "$RECLAMATION_RESPONSE" | jq . 2>/dev/null || echo "$RECLAMATION_RESPONSE"
RECLAMATION_ID=$(echo "$RECLAMATION_RESPONSE" | jq -r '.id' 2>/dev/null || echo "1")
echo ""
echo "Reclamation ID: $RECLAMATION_ID"
echo ""

echo "2.3 Get reclamation by ID:"
curl -s "$RECLAMATION_SERVICE_DIRECT/api/reclamations/$RECLAMATION_ID" | jq . 2>/dev/null || curl -s "$RECLAMATION_SERVICE_DIRECT/api/reclamations/$RECLAMATION_ID"
echo ""
echo ""

echo "2.4 Get reclamations by user ID:"
curl -s "$RECLAMATION_SERVICE_DIRECT/api/reclamations/user/$USER_ID" | jq . 2>/dev/null || curl -s "$RECLAMATION_SERVICE_DIRECT/api/reclamations/user/$USER_ID"
echo ""
echo ""

echo "2.5 Update reclamation status to EN_COURS:"
curl -s -X PATCH "$RECLAMATION_SERVICE_DIRECT/api/reclamations/$RECLAMATION_ID/prendre-en-charge" | jq . 2>/dev/null || curl -s -X PATCH "$RECLAMATION_SERVICE_DIRECT/api/reclamations/$RECLAMATION_ID/prendre-en-charge"
echo ""
echo ""

echo "2.6 Get reclamations by status (EN_COURS):"
curl -s "$RECLAMATION_SERVICE_DIRECT/api/reclamations/statut/EN_COURS" | jq . 2>/dev/null || curl -s "$RECLAMATION_SERVICE_DIRECT/api/reclamations/statut/EN_COURS"
echo ""
echo ""

echo "2.7 Treat reclamation (TRAITEE):"
curl -s -X PATCH "$RECLAMATION_SERVICE_DIRECT/api/reclamations/$RECLAMATION_ID/traiter" | jq . 2>/dev/null || curl -s -X PATCH "$RECLAMATION_SERVICE_DIRECT/api/reclamations/$RECLAMATION_ID/traiter"
echo ""
echo ""

echo "2.8 Get reclamations by status (TRAITEE):"
curl -s "$RECLAMATION_SERVICE_DIRECT/api/reclamations/statut/TRAITEE" | jq . 2>/dev/null || curl -s "$RECLAMATION_SERVICE_DIRECT/api/reclamations/statut/TRAITEE"
echo ""
echo ""

echo "3. Testing API Gateway"
echo "-----------------------------------"
echo "3.1 Get all users through gateway:"
curl -s "$BASE_URL/api/users" | jq . 2>/dev/null || curl -s "$BASE_URL/api/users"
echo ""
echo ""

echo "3.2 Get all reclamations through gateway:"
curl -s "$BASE_URL/api/reclamations" | jq . 2>/dev/null || curl -s "$BASE_URL/api/reclamations"
echo ""
echo ""

echo "=========================================="
echo "API Testing Complete"
echo "=========================================="
