package com.apsit.alumni.connect.controller;

import com.apsit.alumni.connect.dto.NotificationDto;
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // New Import
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Endpoint to get all notifications
    @GetMapping("/me")
    public ResponseEntity<List<NotificationDto>> getMyNotifications(
            @AuthenticationPrincipal User userDetails
    ) {
        List<NotificationDto> notifications = notificationService.getMyNotifications(userDetails.getEmail());
        return ResponseEntity.ok(notifications);
    }

    // --- NEW ENDPOINT ---
    @PostMapping("/me/mark-as-read")
    public ResponseEntity<List<NotificationDto>> markAsRead(
            @AuthenticationPrincipal User userDetails
    ) {
        List<NotificationDto> updatedNotifications = notificationService.markMyNotificationsAsRead(userDetails.getEmail());
        return ResponseEntity.ok(updatedNotifications);
    }
}