package com.example.notificationservice.repository;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReclamationId(Long reclamationId);
    List<Notification> findByUserId(Long userId);
    List<Notification> findByStatus(NotificationStatus status);
}
