package com.example.reclamationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reclamationServiceAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:8082");
        server.setDescription("Reclamation Service - Serveur de développement");

        Contact contact = new Contact();
        contact.setEmail("support@reclamation-system.com");
        contact.setName("Équipe de Support");

        License license = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Reclamation Service API")
                .version("1.0")
                .contact(contact)
                .description("API de gestion des réclamations. " +
                        "Cette API permet de créer, consulter, modifier et supprimer des réclamations, " +
                        "ainsi que de gérer leur cycle de vie (statuts : RECUE, EN_COURS, TRAITEE).")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
