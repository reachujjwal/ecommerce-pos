package com.ujjwal.payload.dto;


import com.ujjwal.domain.PaymentGateway;
import com.ujjwal.domain.PaymentStatus;
import com.ujjwal.domain.SubscriptionStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {

    private Long id;
    private Long storeId;
    private String storeName;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;
    private PaymentStatus paymentStatus;
    private PaymentGateway paymentGateway;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
