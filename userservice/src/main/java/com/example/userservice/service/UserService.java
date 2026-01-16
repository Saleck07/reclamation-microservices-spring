package com.example.userservice.service;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    
    /**
     * Créer un nouveau utilisateur
     */
    public UserDTO createUser(UserRequest request) {
        log.info("Création d'un nouvel utilisateur avec email: {}", request.getEmail());
        
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà: " + request.getEmail());
        }
        
        User user = new User();
        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        user.setTelephone(request.getTelephone());
        
        User savedUser = userRepository.save(user);
        log.info("Utilisateur créé avec succès, ID: {}", savedUser.getId());
        
        return mapToDTO(savedUser);
    }
    
    /**
     * Récupérer tous les utilisateurs
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.info("Récupération de tous les utilisateurs");
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer un utilisateur par ID
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        log.info("Récupération de l'utilisateur avec ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
        return mapToDTO(user);
    }
    
    /**
     * Récupérer un utilisateur par email
     */
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        log.info("Récupération de l'utilisateur avec email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + email));
        return mapToDTO(user);
    }
    
    /**
     * Vérifier si un utilisateur existe
     */
    @Transactional(readOnly = true)
    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }
    
    /**
     * Mapper User vers UserDTO
     */
    private UserDTO mapToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getNom(),
                user.getEmail(),
                user.getTelephone(),
                user.getCreatedAt()
        );
    }
}
