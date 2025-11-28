package com.revcart.dto;

import com.revcart.enums.NotificationType;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDto {
    private String id;
    private NotificationType type;
    private String message;
    private boolean read;
    private Instant createdAt;
}

