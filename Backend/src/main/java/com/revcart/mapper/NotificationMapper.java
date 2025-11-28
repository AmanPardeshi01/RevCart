package com.revcart.mapper;

import com.revcart.document.NotificationDocument;
import com.revcart.dto.NotificationDto;

public final class NotificationMapper {

    private NotificationMapper() {
    }

    public static NotificationDto toDto(NotificationDocument doc) {
        return NotificationDto.builder()
                .id(doc.getId())
                .type(doc.getType())
                .message(doc.getMessage())
                .read(doc.isRead())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}

