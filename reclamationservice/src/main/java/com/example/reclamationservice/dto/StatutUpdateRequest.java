package com.example.reclamationservice.dto;

import com.example.reclamationservice.model.StatutReclamation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatutUpdateRequest {
    private StatutReclamation statut;
}
