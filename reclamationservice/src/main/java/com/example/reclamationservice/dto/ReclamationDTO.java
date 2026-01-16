package com.example.reclamationservice.dto;

import com.example.reclamationservice.model.StatutReclamation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReclamationDTO {
    private Long id;
    private String titre;
    private String description;
    private Long userId;
    private StatutReclamation statut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
