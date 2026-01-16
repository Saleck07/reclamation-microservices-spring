package com.example.reclamationservice.repository;

import com.example.reclamationservice.model.Reclamation;
import com.example.reclamationservice.model.StatutReclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
    
    List<Reclamation> findByUserId(Long userId);
    
    List<Reclamation> findByStatut(StatutReclamation statut);
    
    List<Reclamation> findByUserIdAndStatut(Long userId, StatutReclamation statut);
}
