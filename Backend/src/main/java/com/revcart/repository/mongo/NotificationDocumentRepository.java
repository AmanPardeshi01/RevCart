package com.revcart.repository.mongo;

import com.revcart.document.NotificationDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationDocumentRepository extends MongoRepository<NotificationDocument, String> {
    List<NotificationDocument> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndReadFalse(Long userId);
}

