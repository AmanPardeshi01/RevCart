package com.revcart.document;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "activity_history")
public class ActivityHistory {

    @Id
    private String id;

    private Long userId;

    private String action;

    private Map<String, Object> metadata;

    private Instant timestamp = Instant.now();
}

