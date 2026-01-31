package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationDTO;
import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.model.NotificationStatus;
import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Transactional
    public NotificationDTO createNotification(NotificationRequest request) {
        log.info("Traitement de la notification pour la réclamation {} - Action: {}", 
                 request.getReclamationId(), request.getAction());

        Notification notification = new Notification();
        notification.setReclamationId(request.getReclamationId());
        notification.setUserId(request.getUserId());
        notification.setUserName(request.getUserName());
        notification.setUserEmail(request.getUserEmail());
        
        // Déterminer le type et le contenu de la notification
        NotificationType type = determineNotificationType(request.getAction());
        notification.setType(type);
        notification.setSubject(generateSubject(type, request.getReclamationId()));
        notification.setMessage(generateMessage(type, request.getUserName(), request.getReclamationId()));
        notification.setStatus(NotificationStatus.PENDING);

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification sauvegardée avec ID: {}", savedNotification.getId());

        // Envoyer l'email de manière asynchrone
        sendEmailAsync(savedNotification);

        return convertToDTO(savedNotification);
    }

    @Async
    public void sendEmailAsync(Notification notification) {
        try {
            emailService.sendEmail(
                notification.getUserEmail(),
                notification.getSubject(),
                notification.getMessage()
            );
            
            // Mettre à jour le statut
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email pour la notification {}: {}", 
                     notification.getId(), e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notificationRepository.save(notification);
        }
    }

    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getNotificationsByReclamationId(Long reclamationId) {
        return notificationRepository.findByReclamationId(reclamationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private NotificationType determineNotificationType(String action) {
        return switch (action.toUpperCase()) {
            case "RECUE" -> NotificationType.RECLAMATION_RECUE;
            case "PRISE_EN_CHARGE" -> NotificationType.RECLAMATION_PRISE_EN_CHARGE;
            case "TRAITEE" -> NotificationType.RECLAMATION_TRAITEE;
            default -> throw new IllegalArgumentException("Action non reconnue: " + action);
        };
    }

    private String generateSubject(NotificationType type, Long reclamationId) {
        return switch (type) {
            case RECLAMATION_RECUE -> 
                "Réclamation #" + reclamationId + " - Reçue";
            case RECLAMATION_PRISE_EN_CHARGE -> 
                "Réclamation #" + reclamationId + " - Prise en charge";
            case RECLAMATION_TRAITEE -> 
                "Réclamation #" + reclamationId + " - Traitée";
        };
    }

    private String generateMessage(NotificationType type, String userName, Long reclamationId) {
        return switch (type) {
            case RECLAMATION_RECUE -> 
                String.format("Bonjour %s,\n\nNous avons bien reçu votre réclamation #%d.\n\n" +
                             "Notre équipe va l'examiner dans les plus brefs délais.\n\n" +
                             "Cordialement,\nL'équipe de support", 
                             userName, reclamationId);
            case RECLAMATION_PRISE_EN_CHARGE -> 
                String.format("Bonjour %s,\n\nVotre réclamation #%d est maintenant prise en charge.\n\n" +
                             "Un membre de notre équipe travaille activement sur votre demande.\n\n" +
                             "Cordialement,\nL'équipe de support", 
                             userName, reclamationId);
            case RECLAMATION_TRAITEE -> 
                String.format("Bonjour %s,\n\nVotre réclamation #%d a été traitée.\n\n" +
                             "Nous espérons que la solution apportée vous convient.\n\n" +
                             "Cordialement,\nL'équipe de support", 
                             userName, reclamationId);
        };
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setReclamationId(notification.getReclamationId());
        dto.setUserId(notification.getUserId());
        dto.setUserName(notification.getUserName());
        dto.setUserEmail(notification.getUserEmail());
        dto.setType(notification.getType());
        dto.setSubject(notification.getSubject());
        dto.setMessage(notification.getMessage());
        dto.setStatus(notification.getStatus());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setSentAt(notification.getSentAt());
        dto.setErrorMessage(notification.getErrorMessage());
        return dto;
    }
}
