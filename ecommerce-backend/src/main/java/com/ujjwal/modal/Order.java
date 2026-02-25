package com.ujjwal.modal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ujjwal.domain.DeliveryType;
import com.ujjwal.domain.OrderStatus;
import com.ujjwal.domain.OrderType;
import com.ujjwal.domain.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "order_sequence", allocationSize = 1)
    private Long id;

    private Double totalAmount;

    private LocalDateTime createdAt;

    @ManyToOne
    @JsonIgnore
    private Branch branch;

    @ManyToOne
    @JsonIgnore
    private User cashier;

    @ManyToOne
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentType paymentType;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_category", nullable = false)
    @Builder.Default
    private OrderType orderType = OrderType.IN_STORE; // Default to POS orders

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_mode")
    private DeliveryType deliveryType; // DELIVERY or PICKUP (null for IN_STORE orders)

    @ManyToOne
    @JoinColumn(name = "delivery_address_id")
    private CustomerAddress deliveryAddress; // Null for pickup/in-store orders

    @Column(nullable = false)
    @Builder.Default
    private Double deliveryFee = 0.0; // Delivery charges

    @ManyToOne
    @JoinColumn(name = "assigned_branch_id")
    private Branch assignedBranch; // For online orders, which branch fulfills it

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    @Builder.Default
    private OrderStatus status = OrderStatus.COMPLETED; // Default for POS orders

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
