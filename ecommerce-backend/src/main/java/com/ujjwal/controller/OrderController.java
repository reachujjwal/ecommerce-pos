package com.ujjwal.controller;

import com.ujjwal.domain.OrderStatus;
import com.ujjwal.domain.PaymentType;
import com.ujjwal.exception.UserException;
import com.ujjwal.payload.dto.CheckoutDTO;
import com.ujjwal.payload.dto.OrderDTO;
import com.ujjwal.service.OrderService;
import com.ujjwal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final com.ujjwal.repository.CustomerRepository customerRepository;
    private final com.ujjwal.repository.UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_BRANCH_CASHIER')")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO dto) throws UserException {
        return ResponseEntity.ok(orderService.createOrder(dto));
    }

    // ... existing methods ...

    // Helper method
    private Long getUserCustomerId(String jwt) throws Exception {
        var user = userService.getUserFromJwtToken(jwt);

        if (user.getCustomer() == null) {
            // Self-healing: Create Customer if missing for ROLE_CUSTOMER
            if (user.getRole().toString().equals("ROLE_CUSTOMER")) {
                com.ujjwal.modal.Customer customer = new com.ujjwal.modal.Customer();
                customer.setFullName(user.getFullName());
                customer.setEmail(user.getEmail());
                customer.setPhone(user.getPhone());
                customer.setCreatedAt(java.time.LocalDateTime.now());
                customer.setUpdatedAt(java.time.LocalDateTime.now());

                customer = customerRepository.save(customer);
                user.setCustomer(customer);
                userRepository.save(user);

                return customer.getId();
            }
            throw new Exception("User is not associated with a customer account");
        }

        return user.getCustomer().getId();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByBranch(
            @PathVariable Long branchId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long cashierId,
            @RequestParam(required = false) PaymentType paymentType,
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByBranch(
                branchId,
                customerId,
                cashierId,
                paymentType,
                status));
    }

    @GetMapping("/cashier/{cashierId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCashier(@PathVariable Long cashierId) {
        return ResponseEntity.ok(orderService.getOrdersByCashier(cashierId));
    }

    @GetMapping("/today/branch/{branchId}")
    public ResponseEntity<List<OrderDTO>> getTodayOrders(@PathVariable Long branchId) {
        return ResponseEntity.ok(orderService.getTodayOrdersByBranch(branchId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getCustomerOrders(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @GetMapping("/recent/{branchId}")
    @PreAuthorize("hasAnyAuthority('ROLE_BRANCH_MANAGER', 'ROLE_BRANCH_ADMIN')")
    public ResponseEntity<List<OrderDTO>> getRecentOrders(@PathVariable Long branchId) {
        List<OrderDTO> recentOrders = orderService.getTop5RecentOrdersByBranchId(branchId);
        return ResponseEntity.ok(recentOrders);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STORE_MANAGER') or hasAuthority('ROLE_STORE_ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // ========== E-COMMERCE ENDPOINTS ==========

    /**
     * Customer checkout - Create online order from cart
     */
    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<OrderDTO> checkout(
            @RequestHeader("Authorization") String jwt,
            @RequestBody CheckoutDTO checkoutDTO) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        OrderDTO order = orderService.createOnlineOrder(customerId, checkoutDTO);
        return ResponseEntity.ok(order);
    }

    /**
     * Customer cancel order
     */
    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<OrderDTO> cancelOrder(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        OrderDTO order = orderService.cancelOrder(customerId, orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Get customer's own orders
     */
    @GetMapping("/customer/me")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<OrderDTO>> getMyOrders(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        List<OrderDTO> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get online orders for a branch (for fulfillment)
     */
    @GetMapping("/online/branch/{branchId}")
    @PreAuthorize("hasAnyAuthority('ROLE_BRANCH_MANAGER', 'ROLE_BRANCH_ADMIN', 'ROLE_BRANCH_CASHIER')")
    public ResponseEntity<List<OrderDTO>> getOnlineOrdersByBranch(
            @PathVariable Long branchId,
            @RequestParam(required = false) OrderStatus status) {
        List<OrderDTO> orders = orderService.getOnlineOrdersByBranch(branchId, status);
        return ResponseEntity.ok(orders);
    }

    /**
     * Update order status (for branch staff)
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_BRANCH_MANAGER', 'ROLE_BRANCH_ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) throws Exception {
        OrderDTO order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(order);
    }

    /**
     * Assign order to branch (for store admin/manager)
     */
    @PutMapping("/{orderId}/assign-branch/{branchId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STORE_ADMIN', 'ROLE_STORE_MANAGER')")
    public ResponseEntity<OrderDTO> assignOrderToBranch(
            @PathVariable Long orderId,
            @PathVariable Long branchId) throws Exception {
        OrderDTO order = orderService.assignOrderToBranch(orderId, branchId);
        return ResponseEntity.ok(order);
    }

}
