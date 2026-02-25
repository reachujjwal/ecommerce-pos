package com.ujjwal.payload.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private Long itemId;
    private Long productId;
    private String productName;
    private String productImage;
    private String sku;
    private Integer quantity;
    private Double price;
    private Double total;
    private LocalDateTime addedAt;
    private Integer availableStock; // For stock validation
}
