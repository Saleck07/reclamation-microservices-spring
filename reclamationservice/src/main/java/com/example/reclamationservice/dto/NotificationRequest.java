package com.example.reclamationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private Long reclamationId;
    private String reclamationTitre;
    private Long userId;
    private String userEmail;
    private String userName;
    private String actionType; // RECUE, PRISE_EN_CHARGE, TRAITEE
    private String previousStatus;
    private String newStatus;
}
