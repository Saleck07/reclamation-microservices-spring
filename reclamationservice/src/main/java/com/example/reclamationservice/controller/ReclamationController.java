package com.example.reclamationservice.controller;

import com.example.reclamationservice.dto.ReclamationDTO;
import com.example.reclamationservice.dto.ReclamationRequest;
import com.example.reclamationservice.dto.StatutUpdateRequest;
import com.example.reclamationservice.model.StatutReclamation;
import com.example.reclamationservice.service.ReclamationService;
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
@RequestMapping("/api/reclamations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Réclamations", description = "Gestion des réclamations et de leur cycle de vie")
public class ReclamationController {
    
    private final ReclamationService reclamationService;
    
    /**
     * Créer une nouvelle réclamation
     * POST /api/reclamations
     */
    @PostMapping
    public ResponseEntity<ReclamationDTO> createReclamation(@RequestBody ReclamationRequest request) {
        log.info("Requête de création de réclamation reçue pour l'utilisateur: {}", request.getUserId());
        try {
            ReclamationDTO createdReclamation = reclamationService.createReclamation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReclamation);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création de la réclamation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Récupérer toutes les réclamations
     * GET /api/reclamations
     */
    @GetMapping
    public ResponseEntity<List<ReclamationDTO>> getAllReclamations() {
        log.info("Requête de récupération de toutes les réclamations");
        List<ReclamationDTO> reclamations = reclamationService.getAllReclamations();
        return ResponseEntity.ok(reclamations);
    }
    
    /**
     * Récupérer une réclamation par ID
     * GET /api/reclamations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReclamationDTO> getReclamationById(@PathVariable Long id) {
        log.info("Requête de récupération de la réclamation avec ID: {}", id);
        try {
            ReclamationDTO reclamation = reclamationService.getReclamationById(id);
            return ResponseEntity.ok(reclamation);
        } catch (RuntimeException e) {
            log.error("Réclamation non trouvée: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Récupérer les réclamations d'un utilisateur
     * GET /api/reclamations/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReclamationDTO>> getReclamationsByUserId(@PathVariable Long userId) {
        log.info("Requête de récupération des réclamations pour l'utilisateur: {}", userId);
        List<ReclamationDTO> reclamations = reclamationService.getReclamationsByUserId(userId);
        return ResponseEntity.ok(reclamations);
    }
    
    /**
     * Récupérer les réclamations par statut
     * GET /api/reclamations/statut/{statut}
     */
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<ReclamationDTO>> getReclamationsByStatut(@PathVariable StatutReclamation statut) {
        log.info("Requête de récupération des réclamations avec le statut: {}", statut);
        List<ReclamationDTO> reclamations = reclamationService.getReclamationsByStatut(statut);
        return ResponseEntity.ok(reclamations);
    }
    
    /**
     * Mettre à jour une réclamation complète
     * PUT /api/reclamations/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReclamationDTO> updateReclamation(
            @PathVariable Long id,
            @RequestBody ReclamationRequest request) {
        log.info("Requête de mise à jour complète de la réclamation avec ID: {}", id);
        try {
            ReclamationDTO updatedReclamation = reclamationService.updateReclamation(id, request);
            return ResponseEntity.ok(updatedReclamation);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Mettre à jour le statut d'une réclamation
     * PUT /api/reclamations/{id}/statut
     */
    @PutMapping("/{id}/statut")
    public ResponseEntity<ReclamationDTO> updateStatut(
            @PathVariable Long id,
            @RequestBody StatutUpdateRequest request) {
        log.info("Requête de mise à jour du statut de la réclamation {} vers {}", id, request.getStatut());
        try {
            ReclamationDTO updatedReclamation = reclamationService.updateStatut(id, request.getStatut());
            return ResponseEntity.ok(updatedReclamation);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour du statut: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Prendre en charge une réclamation (RECUE -> EN_COURS)
     * PATCH /api/reclamations/{id}/prendre-en-charge
     */
    @PatchMapping("/{id}/prendre-en-charge")
    public ResponseEntity<ReclamationDTO> prendreEnCharge(@PathVariable Long id) {
        log.info("Requête de prise en charge de la réclamation: {}", id);
        try {
            ReclamationDTO reclamation = reclamationService.prendreEnCharge(id);
            return ResponseEntity.ok(reclamation);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la prise en charge: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Traiter une réclamation (-> TRAITEE)
     * PATCH /api/reclamations/{id}/traiter
     */
    @PatchMapping("/{id}/traiter")
    public ResponseEntity<ReclamationDTO> traiterReclamation(@PathVariable Long id) {
        log.info("Requête de traitement de la réclamation: {}", id);
        try {
            ReclamationDTO reclamation = reclamationService.traiterReclamation(id);
            return ResponseEntity.ok(reclamation);
        } catch (RuntimeException e) {
            log.error("Erreur lors du traitement: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Supprimer une réclamation
     * DELETE /api/reclamations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReclamation(@PathVariable Long id) {
        log.info("Requête de suppression de la réclamation avec ID: {}", id);
        try {
            reclamationService.deleteReclamation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
