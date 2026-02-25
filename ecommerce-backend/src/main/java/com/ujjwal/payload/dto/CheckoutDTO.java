package com.ujjwal.payload.dto;

import com.ujjwal.domain.DeliveryType;
import com.ujjwal.domain.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutDTO {

    @NotNull(message = "Delivery type is required")
    private DeliveryType deliveryType; // DELIVERY or PICKUP

    private Long addressId; // Required if deliveryType = DELIVERY

    private Long branchId; // Required if deliveryType = PICKUP

    @NotNull(message = "Payment type is required")
    private PaymentType paymentType; // CASH, CARD, UPI, CASH_ON_DELIVERY, ONLINE

    private String notes; // Optional customer notes
}
