package com.example.reclamationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReclamationRequest {
    private String titre;
    private String description;
    private Long userId;
}
