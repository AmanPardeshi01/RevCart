package com.revcart.dto.request;

import com.revcart.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {

    @NotNull
    private OrderStatus status;

    private String note;
}

