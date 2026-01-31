package com.example.notificationservice.controller;

import com.example.notificationservice.dto.NotificationDTO;
import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Management", description = "APIs pour gérer les notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Créer une notification", description = "Créer et envoyer une notification par email")
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationRequest request) {
        try {
            log.info("Réception d'une demande de notification pour la réclamation: {}", request.getReclamationId());
            NotificationDTO notification = notificationService.createNotification(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (Exception e) {
            log.error("Erreur lors de la création de la notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les notifications")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        log.info("Requête de récupération de toutes les notifications");
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/reclamation/{reclamationId}")
    @Operation(summary = "Récupérer les notifications par réclamation")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByReclamationId(@PathVariable Long reclamationId) {
        log.info("Requête de récupération des notifications pour la réclamation: {}", reclamationId);
        List<NotificationDTO> notifications = notificationService.getNotificationsByReclamationId(reclamationId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Récupérer les notifications par utilisateur")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(@PathVariable Long userId) {
        log.info("Requête de récupération des notifications pour l'utilisateur: {}", userId);
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }
}
