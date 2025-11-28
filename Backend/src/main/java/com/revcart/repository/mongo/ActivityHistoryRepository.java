package com.revcart.repository.mongo;

import com.revcart.document.ActivityHistory;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityHistoryRepository extends MongoRepository<ActivityHistory, String> {
    List<ActivityHistory> findByUserIdOrderByTimestampDesc(Long userId);
}

