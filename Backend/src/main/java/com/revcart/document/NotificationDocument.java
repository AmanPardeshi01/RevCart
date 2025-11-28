package com.revcart.document;

import com.revcart.enums.NotificationType;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "notifications")
public class NotificationDocument {

    @Id
    private String id;

    private Long userId;

    private String message;

    private NotificationType type;

    private boolean read = false;

    private Instant createdAt = Instant.now();
}

