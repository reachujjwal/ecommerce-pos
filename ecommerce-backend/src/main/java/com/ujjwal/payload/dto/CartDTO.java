package com.ujjwal.payload.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private Long cartId;
    private Long customerId;
    private List<CartItemDTO> items;
    private Double subtotal;
    private Integer itemCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
