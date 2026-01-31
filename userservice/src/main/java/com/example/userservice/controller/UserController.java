package com.example.userservice.controller;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Utilisateurs", description = "Gestion des utilisateurs du système de réclamations")
public class UserController {
    
    private final UserService userService;
    
    /**
     * Créer un nouveau utilisateur
     * POST /api/users
     */
    @Operation(summary = "Créer un utilisateur", description = "Crée un nouveau utilisateur dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (email déjà existant)")
    })
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRequest request) {
        log.info("Requête de création d'utilisateur reçue: {}", request.getEmail());
        try {
            UserDTO createdUser = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création de l'utilisateur: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Récupérer tous les utilisateurs
     * GET /api/users
     */
    @Operation(summary = "Lister tous les utilisateurs", description = "Récupère la liste de tous les utilisateurs")
    @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Requête de récupération de tous les utilisateurs");
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Récupérer un utilisateur par ID
     * GET /api/users/{id}
     */
    @Operation(summary = "Obtenir un utilisateur par ID", description = "Récupère les détails d'un utilisateur spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id) {
        log.info("Requête de récupération de l'utilisateur avec ID: {}", id);
        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            log.error("Utilisateur non trouvé: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Récupérer un utilisateur par email
     * GET /api/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        log.info("Requête de récupération de l'utilisateur avec email: {}", email);
        try {
            UserDTO user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            log.error("Utilisateur non trouvé: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Vérifier si un utilisateur existe
     * GET /api/users/{id}/exists
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> userExists(@PathVariable Long id) {
        log.info("Vérification de l'existence de l'utilisateur avec ID: {}", id);
        boolean exists = userService.userExists(id);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * Mettre à jour un utilisateur
     * PUT /api/users/{id}
     */
    @Operation(summary = "Mettre à jour un utilisateur", description = "Modifie les informations d'un utilisateur existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id,
            @RequestBody UserRequest request) {
        log.info("Requête de mise à jour de l'utilisateur avec ID: {}", id);
        try {
            UserDTO updatedUser = userService.updateUser(id, request);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Supprimer un utilisateur
     * DELETE /api/users/{id}
     */
    @Operation(summary = "Supprimer un utilisateur", description = "Supprime un utilisateur du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id) {
        log.info("Requête de suppression de l'utilisateur avec ID: {}", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
