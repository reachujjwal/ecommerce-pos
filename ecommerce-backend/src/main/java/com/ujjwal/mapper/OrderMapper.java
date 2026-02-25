package com.ujjwal.mapper;

import com.ujjwal.modal.Order;
import com.ujjwal.payload.dto.OrderDTO;

import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDTO toDto(Order order) {
        if (order == null)
            return null;

        Long branchId = null;
        if (order.getBranch() != null) {
            branchId = order.getBranch().getId();
        } else if (order.getAssignedBranch() != null) {
            branchId = order.getAssignedBranch().getId();
        }

        return OrderDTO.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .branchId(branchId)
                .cashierId(order.getCashier() != null ? order.getCashier().getId() : null)
                .customer(order.getCustomer())
                .createdAt(order.getCreatedAt())
                .paymentType(order.getPaymentType())
                .status(order.getStatus())
                .items(order.getItems() != null ? order.getItems().stream()
                        .map(OrderItemMapper::toDto)
                        .collect(Collectors.toList()) : java.util.Collections.emptyList())
                .build();
    }
}
