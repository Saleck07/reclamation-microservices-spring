package com.example.reclamationservice.model;

public enum StatutReclamation {
    RECUE("Réclamation reçue"),
    EN_COURS("Réclamation en cours de traitement"),
    TRAITEE("Réclamation traitée");
    
    private final String description;
    
    StatutReclamation(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
