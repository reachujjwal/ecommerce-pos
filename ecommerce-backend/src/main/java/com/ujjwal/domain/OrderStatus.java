package com.ujjwal.domain;

public enum OrderStatus {
    // POS orders
    COMPLETED,
    PENDING,
    REFUNDED,
    CANCELLED,

    // Online order lifecycle
    CONFIRMED, // Order confirmed, awaiting processing
    PROCESSING, // Being prepared
    READY_FOR_PICKUP, // Ready for customer pickup
    OUT_FOR_DELIVERY, // Out for delivery
    DELIVERED // Successfully delivered
}
