package com.apsit.alumni.connect.service;

import com.apsit.alumni.connect.dto.NotificationDto;
import com.apsit.alumni.connect.model.Notification;
import com.apsit.alumni.connect.model.NotificationType;
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.repository.NotificationRepository;
import com.apsit.alumni.connect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // New Import

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // Helper method to convert Entity to DTO
    private NotificationDto mapToDto(Notification notification) {
        // ... (this method is unchanged)
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setType(notification.getType().toString());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setReadStatus(notification.isReadStatus());
        dto.setRelatedEntityId(notification.getRelatedEntityId());
        dto.setCreatedAt(notification.getCreatedAt().toString());
        return dto;
    }

    // Method for other services to create notifications
    public void createNotification(User user, NotificationType type, String title, String message, Long relatedEntityId) {
        // ... (this method is unchanged)
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRelatedEntityId(relatedEntityId);
        notificationRepository.save(notification);
    }

    // Method for a user to fetch their notifications
    public List<NotificationDto> getMyNotifications(String userEmail) {
        // ... (this method is unchanged)
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        return notifications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // --- NEW METHOD ---
    @Transactional // Ensure all notifications are saved together
    public List<NotificationDto> markMyNotificationsAsRead(String userEmail) {
        // 1. Find the user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Find only the UNREAD notifications
        List<Notification> unreadNotifications = notificationRepository.findByUserAndReadStatus(user, false);

        if (unreadNotifications.isEmpty()) {
            return List.of(); // Return an empty list if there's nothing to update
        }

        // 3. Loop and update the status
        for (Notification notification : unreadNotifications) {
            notification.setReadStatus(true);
        }

        // 4. Save all changes to the database
        List<Notification> savedNotifications = notificationRepository.saveAll(unreadNotifications);

        // 5. Convert to DTOs and return the list of notifications that were just updated
        return savedNotifications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}