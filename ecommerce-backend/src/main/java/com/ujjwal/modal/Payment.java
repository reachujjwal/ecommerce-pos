package com.ujjwal.modal;

import com.ujjwal.domain.PaymentGateway;
import com.ujjwal.domain.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Store store;

    // Relation with booking
    @OneToOne(fetch = FetchType.LAZY)
    private Subscription subscription;

    // Amount paid
    private Double amount;

    // Payment provider (Stripe, Razorpay, PayPal, etc.)
    private PaymentGateway provider;

    private String providerPaymentId;

    // Transaction ID from provider
    private String transactionId;

    // Payment method (CARD, UPI, NETBANKING, WALLET...)
    private String method;

    // Status (PENDING, SUCCESS, FAILED, REFUNDED)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String failureReason;

    // Payment timestamp
    private LocalDateTime paidAt;

    // Refund reference (if refunded)
    private String refundId;


    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }
}
