package com.smartwaste.backend.controller;

import com.smartwaste.backend.entity.Notification;
import com.smartwaste.backend.repository.NotificationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(
            NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Get notifications for a user
    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(
            @PathVariable Long userId) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Mark notification as read
    @PutMapping("/{id}/read")
    public Notification markAsRead(
            @PathVariable Long id) {

        Notification notification =
                notificationRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Notification not found"
                                )
                        );

        notification.setRead(true);

        return notificationRepository.save(notification);
    }
}