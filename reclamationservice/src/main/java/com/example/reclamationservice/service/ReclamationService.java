package com.example.reclamationservice.service;

import com.example.reclamationservice.dto.ReclamationDTO;
import com.example.reclamationservice.dto.ReclamationRequest;
import com.example.reclamationservice.model.Reclamation;
import com.example.reclamationservice.model.StatutReclamation;
import com.example.reclamationservice.repository.ReclamationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReclamationService {
    
    private final ReclamationRepository reclamationRepository;
    private final UserServiceClient userServiceClient;
    
    /**
     * Créer une nouvelle réclamation
     */
    public ReclamationDTO createReclamation(ReclamationRequest request) {
        log.info("Création d'une nouvelle réclamation pour l'utilisateur: {}", request.getUserId());
        
        // Vérifier si l'utilisateur existe via User Service
        if (!userServiceClient.userExists(request.getUserId())) {
            throw new RuntimeException("L'utilisateur avec l'ID " + request.getUserId() + " n'existe pas");
        }
        
        Reclamation reclamation = new Reclamation();
        reclamation.setTitre(request.getTitre());
        reclamation.setDescription(request.getDescription());
        reclamation.setUserId(request.getUserId());
        reclamation.setStatut(StatutReclamation.RECUE);
        
        Reclamation savedReclamation = reclamationRepository.save(reclamation);
        log.info("Réclamation créée avec succès, ID: {}, Statut: {}", 
                savedReclamation.getId(), savedReclamation.getStatut());
        
        return mapToDTO(savedReclamation);
    }
    
    /**
     * Récupérer toutes les réclamations
     */
    @Transactional(readOnly = true)
    public List<ReclamationDTO> getAllReclamations() {
        log.info("Récupération de toutes les réclamations");
        return reclamationRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer une réclamation par ID
     */
    @Transactional(readOnly = true)
    public ReclamationDTO getReclamationById(Long id) {
        log.info("Récupération de la réclamation avec ID: {}", id);
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée avec l'ID: " + id));
        return mapToDTO(reclamation);
    }
    
    /**
     * Récupérer les réclamations d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<ReclamationDTO> getReclamationsByUserId(Long userId) {
        log.info("Récupération des réclamations pour l'utilisateur: {}", userId);
        return reclamationRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les réclamations par statut
     */
    @Transactional(readOnly = true)
    public List<ReclamationDTO> getReclamationsByStatut(StatutReclamation statut) {
        log.info("Récupération des réclamations avec le statut: {}", statut);
        return reclamationRepository.findByStatut(statut)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Mettre à jour une réclamation complète
     */
    public ReclamationDTO updateReclamation(Long id, ReclamationRequest request) {
        log.info("Mise à jour complète de la réclamation avec ID: {}", id);
        
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée avec l'ID: " + id));
        
        // Si l'utilisateur change, vérifier qu'il existe
        if (!reclamation.getUserId().equals(request.getUserId())) {
            if (!userServiceClient.userExists(request.getUserId())) {
                throw new RuntimeException("L'utilisateur avec l'ID " + request.getUserId() + " n'existe pas");
            }
        }
        
        reclamation.setTitre(request.getTitre());
        reclamation.setDescription(request.getDescription());
        reclamation.setUserId(request.getUserId());
        
        Reclamation updatedReclamation = reclamationRepository.save(reclamation);
        log.info("Réclamation mise à jour avec succès, ID: {}", updatedReclamation.getId());
        
        return mapToDTO(updatedReclamation);
    }
    
    /**
     * Mettre à jour le statut d'une réclamation
     */
    public ReclamationDTO updateStatut(Long id, StatutReclamation newStatut) {
        log.info("Mise à jour du statut de la réclamation {} vers {}", id, newStatut);
        
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée avec l'ID: " + id));
        
        StatutReclamation oldStatut = reclamation.getStatut();
        reclamation.setStatut(newStatut);
        
        Reclamation updatedReclamation = reclamationRepository.save(reclamation);
        log.info("Statut de la réclamation {} mis à jour: {} -> {}", 
                id, oldStatut, newStatut);
        
        return mapToDTO(updatedReclamation);
    }
    
    /**
     * Prendre en charge une réclamation (passer de RECUE à EN_COURS)
     */
    public ReclamationDTO prendreEnCharge(Long id) {
        log.info("Prise en charge de la réclamation: {}", id);
        
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée avec l'ID: " + id));
        
        if (reclamation.getStatut() != StatutReclamation.RECUE) {
            throw new RuntimeException("La réclamation doit être au statut RECUE pour être prise en charge");
        }
        
        return updateStatut(id, StatutReclamation.EN_COURS);
    }
    
    /**
     * Traiter une réclamation (passer à TRAITEE)
     */
    public ReclamationDTO traiterReclamation(Long id) {
        log.info("Traitement de la réclamation: {}", id);
        
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée avec l'ID: " + id));
        
        if (reclamation.getStatut() == StatutReclamation.TRAITEE) {
            throw new RuntimeException("La réclamation est déjà traitée");
        }
        
        return updateStatut(id, StatutReclamation.TRAITEE);
    }
    
    /**
     * Supprimer une réclamation
     */
    public void deleteReclamation(Long id) {
        log.info("Suppression de la réclamation avec ID: {}", id);
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée avec l'ID: " + id));
        reclamationRepository.delete(reclamation);
        log.info("Réclamation supprimée avec succès, ID: {}", id);
    }
    
    /**
     * Mapper Reclamation vers ReclamationDTO
     */
    private ReclamationDTO mapToDTO(Reclamation reclamation) {
        return new ReclamationDTO(
                reclamation.getId(),
                reclamation.getTitre(),
                reclamation.getDescription(),
                reclamation.getUserId(),
                reclamation.getStatut(),
                reclamation.getCreatedAt(),
                reclamation.getUpdatedAt()
        );
    }
}
