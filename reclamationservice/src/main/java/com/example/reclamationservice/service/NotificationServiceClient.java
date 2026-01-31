package com.example.reclamationservice.service;

import com.example.reclamationservice.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    
    /**
     * Envoyer une notification de manière asynchrone au service de notification
     */
    @Async
    public void sendNotification(NotificationRequest request) {
        try {
            log.info("Envoi de la notification au Notification Service pour la réclamation: {}", 
                    request.getReclamationId());
            
            webClientBuilder.build()
                    .post()
                    .uri("http://notification-service/api/notifications/send")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .subscribe(
                            result -> log.info("Notification envoyée avec succès pour la réclamation: {}", 
                                    request.getReclamationId()),
                            error -> log.error("Erreur lors de l'envoi de la notification: {}", error.getMessage())
                    );
                    
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification au service: {}", e.getMessage());
        }
    }
}
