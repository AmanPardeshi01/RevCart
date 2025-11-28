package com.revcart.document;

import com.revcart.enums.OrderStatus;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "delivery_tracking_logs")
public class DeliveryTrackingLog {

    @Id
    private String id;

    private Long orderId;

    private OrderStatus status;

    private String location;

    private String note;

    private Instant timestamp = Instant.now();
}

