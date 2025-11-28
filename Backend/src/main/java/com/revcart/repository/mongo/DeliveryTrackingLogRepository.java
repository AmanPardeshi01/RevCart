package com.revcart.repository.mongo;

import com.revcart.document.DeliveryTrackingLog;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeliveryTrackingLogRepository extends MongoRepository<DeliveryTrackingLog, String> {
    List<DeliveryTrackingLog> findByOrderIdOrderByTimestampAsc(Long orderId);
}

