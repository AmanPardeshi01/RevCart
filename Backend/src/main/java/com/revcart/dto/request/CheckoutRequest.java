package com.revcart.dto.request;

import com.revcart.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class CheckoutRequest {

    @NotNull
    private Long addressId;

    private PaymentMethod paymentMethod;

    private List<CartItemRequest> itemsOverride;
}

