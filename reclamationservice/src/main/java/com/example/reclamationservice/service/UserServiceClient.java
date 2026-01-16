package com.example.reclamationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {
    
    private final WebClient.Builder webClientBuilder;
    
    /**
     * Vérifier si un utilisateur existe dans le User Service
     */
    public boolean userExists(Long userId) {
        log.info("Vérification de l'existence de l'utilisateur avec ID: {} via User Service", userId);
        
        try {
            Boolean exists = webClientBuilder.build()
                    .get()
                    .uri("http://user-service/api/users/{id}/exists", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            
            log.info("Résultat de la vérification pour l'utilisateur {}: {}", userId, exists);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de l'utilisateur {}: {}", userId, e.getMessage());
            throw new RuntimeException("Impossible de vérifier l'existence de l'utilisateur: " + e.getMessage());
        }
    }
}
