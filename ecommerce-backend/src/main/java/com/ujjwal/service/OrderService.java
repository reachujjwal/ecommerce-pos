package com.ujjwal.service;

import com.ujjwal.domain.OrderStatus;
import com.ujjwal.domain.PaymentType;
import com.ujjwal.exception.UserException;
import com.ujjwal.payload.dto.CheckoutDTO;
import com.ujjwal.payload.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderDTO dto) throws UserException;

    OrderDTO getOrderById(Long id);

    List<OrderDTO> getOrdersByBranch(Long branchId,
            Long customerId,
            Long cashierId,
            PaymentType paymentType,
            OrderStatus status);

    List<OrderDTO> getOrdersByCashier(Long cashierId);

    void deleteOrder(Long id);

    List<OrderDTO> getTodayOrdersByBranch(Long branchId);

    List<OrderDTO> getOrdersByCustomerId(Long customerId);

    List<OrderDTO> getTop5RecentOrdersByBranchId(Long branchId);

    // E-commerce methods
    OrderDTO createOnlineOrder(Long customerId, CheckoutDTO checkoutDTO) throws Exception;

    OrderDTO cancelOrder(Long customerId, Long orderId) throws Exception;

    OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) throws Exception;

    List<OrderDTO> getOnlineOrdersByBranch(Long branchId, OrderStatus status);

    OrderDTO assignOrderToBranch(Long orderId, Long branchId) throws Exception;
}
