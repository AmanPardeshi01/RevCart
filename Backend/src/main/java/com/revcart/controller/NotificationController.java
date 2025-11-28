package com.revcart.controller;

import com.revcart.dto.ApiResponse;
import com.revcart.dto.NotificationDto;
import com.revcart.service.NotificationService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationDto> list() {
        return notificationService.getNotifications();
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> unreadCount() {
        long unread = notificationService.unreadCount();
        return ApiResponse.<Long>builder().success(true).data(unread).build();
    }

    @PostMapping("/{id}/read")
    public ApiResponse<String> markRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ApiResponse.<String>builder().success(true).message("Marked as read").build();
    }
}

