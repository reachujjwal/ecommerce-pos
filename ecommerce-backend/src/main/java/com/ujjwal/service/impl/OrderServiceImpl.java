package com.ujjwal.service.impl;

import com.ujjwal.domain.DeliveryType;
import com.ujjwal.domain.OrderStatus;
import com.ujjwal.domain.OrderType;
import com.ujjwal.domain.PaymentType;
import com.ujjwal.exception.UserException;
import com.ujjwal.mapper.OrderMapper;
import com.ujjwal.modal.*;
import com.ujjwal.payload.dto.CheckoutDTO;
import com.ujjwal.payload.dto.OrderDTO;
import com.ujjwal.repository.*;

import com.ujjwal.service.CartService;
import com.ujjwal.service.EmailTemplateService;
import com.ujjwal.service.OrderService;
import com.ujjwal.service.UserService;
import com.ujjwal.util.BranchAccessValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;
        private final ProductRepository productRepository;
        private final BranchRepository branchRepository;
        private final UserService userService;
        private final CartService cartService;
        private final CartRepository cartRepository;
        private final CustomerRepository customerRepository;
        private final CustomerAddressRepository addressRepository;
        private final InventoryRepository inventoryRepository;
        private final EmailTemplateService emailTemplateService;

        @Override
        @Transactional
        public OrderDTO createOrder(OrderDTO dto) throws UserException {
                User cashier = userService.getCurrentUser();

                Branch branch = cashier.getBranch();

                if (branch == null) {
                        throw new UserException("cashier's branch is null");
                }

                Order order = Order.builder()
                                .branch(branch)
                                .cashier(cashier)
                                .customer(dto.getCustomer())
                                .paymentType(dto.getPaymentType())
                                .build();

                List<OrderItem> orderItems = dto.getItems().stream().map(itemDto -> {
                        Product product = productRepository.findById(itemDto.getProductId())
                                        .orElseThrow(() -> new EntityNotFoundException("Product not found"));

                        return OrderItem.builder()
                                        .product(product)
                                        .quantity(itemDto.getQuantity())
                                        .price(product.getSellingPrice() * itemDto.getQuantity())
                                        .order(order)

                                        .build();
                }).toList();

                double total = orderItems.stream().mapToDouble(OrderItem::getPrice).sum();
                order.setTotalAmount(total);
                order.setItems(orderItems);

                // Enforce branch-specific inventory for POS orders
                try {
                        validateInventoryForOrder(order, branch);
                        deductInventoryForOrder(order, branch);
                } catch (Exception e) {
                        throw new UserException(e.getMessage());
                }

                return OrderMapper.toDto(orderRepository.save(order));
        }

        @Override
        public OrderDTO getOrderById(Long id) {
                return orderRepository.findById(id)
                                .map(OrderMapper::toDto)
                                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        }

        @Override
        public List<OrderDTO> getOrdersByBranch(Long branchId,
                        Long customerId,
                        Long cashierId,
                        PaymentType paymentType,
                        OrderStatus status) {
                return orderRepository.findByBranchId(branchId).stream()

                                // ✅ Filter by Customer ID (if provided)
                                .filter(order -> customerId == null ||
                                                (order.getCustomer() != null &&
                                                                order.getCustomer().getId().equals(customerId)))

                                // ✅ Filter by Cashier ID (if provided)
                                .filter(order -> cashierId == null ||
                                                (order.getCashier() != null &&
                                                                order.getCashier().getId().equals(cashierId)))

                                // ✅ Filter by Payment Type (if provided)
                                .filter(order -> paymentType == null ||
                                                order.getPaymentType() == paymentType)

                                // ✅ Filter by Status (if provided)
                                // .filter(order -> status() == null ||
                                // order.getStatus() == status)

                                // ✅ Map to DTO
                                .map(OrderMapper::toDto)

                                // ✅ Sort by createdAt (latest first)
                                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))

                                .collect(Collectors.toList());
                // return orderRepository.findByBranchId(branchId).stream()
                // .map(OrderMapper::toDto)
                // .collect(Collectors.toList());
        }

        @Override
        public List<OrderDTO> getOrdersByCashier(Long cashierId) {
                return orderRepository.findByCashierId(cashierId).stream()
                                .map(OrderMapper::toDto)
                                .collect(Collectors.toList());
        }

        @Override
        public void deleteOrder(Long id) {
                if (!orderRepository.existsById(id)) {
                        throw new EntityNotFoundException("Order not found");
                }
                orderRepository.deleteById(id);
        }

        @Override
        public List<OrderDTO> getTodayOrdersByBranch(Long branchId) {
                LocalDate today = LocalDate.now();
                LocalDateTime start = today.atStartOfDay();
                LocalDateTime end = today.plusDays(1).atStartOfDay();

                return orderRepository.findByBranchIdAndCreatedAtBetween(branchId, start, end)
                                .stream()
                                .map(OrderMapper::toDto)
                                .collect(Collectors.toList());
        }

        @Override
        public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
                List<Order> orders = orderRepository.findByCustomerId(customerId);

                return orders.stream()
                                .map(OrderMapper::toDto)
                                .collect(Collectors.toList());
        }

        @Override
        public List<OrderDTO> getTop5RecentOrdersByBranchId(Long branchId) {
                branchRepository.findById(branchId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Branch not found with ID: " + branchId));

                List<Order> orders = orderRepository.findTop5ByBranchIdOrderByCreatedAtDesc(branchId);
                return orders.stream()
                                .map(OrderMapper::toDto)
                                .collect(Collectors.toList());
        }

        // ========== E-COMMERCE METHODS ==========

        @Override
        @Transactional
        public OrderDTO createOnlineOrder(Long customerId, CheckoutDTO checkoutDTO) throws Exception {
                // 1. Get customer
                Customer customer = customerRepository.findById(customerId)
                                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

                // 2. Get cart
                Cart cart = cartRepository.findActiveCartByCustomerId(customerId, LocalDateTime.now())
                                .orElseThrow(() -> new Exception("No active cart found"));

                if (cart.getItems().isEmpty()) {
                        throw new Exception("Cart is empty");
                }

                // 3. Validate delivery details
                CustomerAddress deliveryAddress = null;
                Branch assignedBranch = null;
                Double deliveryFee = 0.0;

                if (checkoutDTO.getDeliveryType() == DeliveryType.DELIVERY) {
                        if (checkoutDTO.getAddressId() == null) {
                                throw new Exception("Delivery address is required for delivery orders");
                        }
                        deliveryAddress = addressRepository
                                        .findByIdAndCustomerId(checkoutDTO.getAddressId(), customerId)
                                        .orElseThrow(() -> new Exception("Delivery address not found"));
                        deliveryFee = 50.0; // Fixed delivery fee, can be made dynamic
                } else if (checkoutDTO.getDeliveryType() == DeliveryType.PICKUP) {
                        if (checkoutDTO.getBranchId() == null) {
                                throw new Exception("Branch is required for pickup orders");
                        }
                        assignedBranch = branchRepository.findById(checkoutDTO.getBranchId())
                                        .orElseThrow(() -> new Exception("Branch not found"));
                }

                // 4. Validate inventory (if assigned branch is known)
                if (assignedBranch != null) {
                        validateInventory(cart, assignedBranch);
                }

                // 5. Create order items from cart
                Order order = Order.builder()
                                .customer(customer)
                                .orderType(OrderType.ONLINE)
                                .deliveryType(checkoutDTO.getDeliveryType())
                                .deliveryAddress(deliveryAddress)
                                .deliveryFee(deliveryFee)
                                .assignedBranch(assignedBranch)
                                .paymentType(checkoutDTO.getPaymentType())
                                .status(OrderStatus.CONFIRMED)
                                .build();

                List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
                        return OrderItem.builder()
                                        .product(cartItem.getProduct())
                                        .quantity(cartItem.getQuantity())
                                        .price(cartItem.getPriceSnapshot() * cartItem.getQuantity())
                                        .order(order)
                                        .build();
                }).toList();

                // 6. Calculate total
                double subtotal = orderItems.stream().mapToDouble(OrderItem::getPrice).sum();
                double total = subtotal + deliveryFee;
                order.setTotalAmount(total);
                order.setItems(orderItems);

                // 7. Save order
                Order savedOrder = orderRepository.save(order);

                // 8. Deduct inventory if branch is assigned
                if (assignedBranch != null) {
                        deductInventory(cart, assignedBranch);
                }

                // 9. Clear cart
                cartService.clearCart(customerId);

                // 10. Send Email Notification
                try {
                        sendOrderPlacementEmail(savedOrder);
                } catch (Exception e) {
                        // Log error but don't fail the order creation
                        System.err.println("Failed to send order placement email: " + e.getMessage());
                }

                return OrderMapper.toDto(savedOrder);
        }

        private void sendOrderPlacementEmail(Order order) {
                java.util.Map<String, Object> context = new java.util.HashMap<>();
                context.put("customerName", order.getCustomer().getFullName());
                context.put("orderId", order.getId());
                context.put("totalAmount", String.format("%.2f", order.getTotalAmount()));
                context.put("deliveryType", order.getDeliveryType());

                StringBuilder itemsTable = new StringBuilder();
                itemsTable.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
                itemsTable.append("<tr><th>Item</th><th>Qty</th><th>Price</th></tr>");
                for (OrderItem item : order.getItems()) {
                        itemsTable.append("<tr>")
                                        .append("<td>").append(item.getProduct().getName()).append("</td>")
                                        .append("<td>").append(item.getQuantity()).append("</td>")
                                        .append("<td>").append(String.format("%.2f", item.getPrice())).append("</td>")
                                        .append("</tr>");
                }
                itemsTable.append("</table>");

                context.put("itemsTable", itemsTable.toString());

                emailTemplateService.sendDynamicEmail(
                                order.getCustomer().getEmail(),
                                "ORDER_PLACEMENT",
                                context);
        }

        @Override
        @Transactional
        public OrderDTO cancelOrder(Long customerId, Long orderId) throws Exception {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

                // Verify order belongs to customer
                if (!order.getCustomer().getId().equals(customerId)) {
                        throw new Exception("Order does not belong to this customer");
                }

                // Only allow cancellation for certain statuses
                if (order.getStatus() == OrderStatus.DELIVERED ||
                                order.getStatus() == OrderStatus.CANCELLED) {
                        throw new Exception("Cannot cancel order with status: " + order.getStatus());
                }

                // Restore inventory if it was deducted
                if (order.getAssignedBranch() != null) {
                        restoreInventory(order);
                }

                order.setStatus(OrderStatus.CANCELLED);
                Order savedOrder = orderRepository.save(order);

                return OrderMapper.toDto(savedOrder);
        }

        @Override
        @Transactional
        public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) throws Exception {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

                // Validate status transition
                validateStatusTransition(order.getStatus(), newStatus);

                order.setStatus(newStatus);
                Order savedOrder = orderRepository.save(order);

                return OrderMapper.toDto(savedOrder);
        }

        @Override
        public List<OrderDTO> getOnlineOrdersByBranch(Long branchId, OrderStatus status) {
                return orderRepository.findByAssignedBranchId(branchId).stream()
                                .filter(order -> order.getOrderType() == OrderType.ONLINE)
                                .filter(order -> status == null || order.getStatus() == status)
                                .map(OrderMapper::toDto)
                                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public OrderDTO assignOrderToBranch(Long orderId, Long branchId) throws Exception {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

                if (order.getOrderType() != OrderType.ONLINE) {
                        throw new Exception("Only online orders can be assigned to branches");
                }

                Branch branch = branchRepository.findById(branchId)
                                .orElseThrow(() -> new EntityNotFoundException("Branch not found"));

                // Validate inventory before assignment
                validateInventoryForOrder(order, branch);

                order.setAssignedBranch(branch);

                // Deduct inventory
                deductInventoryForOrder(order, branch);

                Order savedOrder = orderRepository.save(order);
                return OrderMapper.toDto(savedOrder);
        }

        // ========== HELPER METHODS ==========

        private void validateInventory(Cart cart, Branch branch) throws Exception {
                for (var cartItem : cart.getItems()) {
                        Inventory inventory = inventoryRepository.findByProductIdAndBranchId(
                                        cartItem.getProduct().getId(), branch.getId());

                        if (inventory == null || inventory.getQuantity() < cartItem.getQuantity()) {
                                throw new Exception(
                                                "Insufficient stock for product: " + cartItem.getProduct().getName());
                        }
                }
        }

        private void deductInventory(Cart cart, Branch branch) {
                for (var cartItem : cart.getItems()) {
                        Inventory inventory = inventoryRepository.findByProductIdAndBranchId(
                                        cartItem.getProduct().getId(), branch.getId());

                        if (inventory != null) {
                                inventory.setQuantity(inventory.getQuantity() - cartItem.getQuantity());
                                inventoryRepository.save(inventory);
                        }
                }
        }

        private void restoreInventory(Order order) {
                if (order.getAssignedBranch() == null)
                        return;

                for (var orderItem : order.getItems()) {
                        Inventory inventory = inventoryRepository.findByProductIdAndBranchId(
                                        orderItem.getProduct().getId(), order.getAssignedBranch().getId());

                        if (inventory != null) {
                                inventory.setQuantity(inventory.getQuantity() + orderItem.getQuantity());
                                inventoryRepository.save(inventory);
                        }
                }
        }

        private void validateInventoryForOrder(Order order, Branch branch) throws Exception {
                for (var orderItem : order.getItems()) {
                        Inventory inventory = inventoryRepository.findByProductIdAndBranchId(
                                        orderItem.getProduct().getId(), branch.getId());

                        if (inventory == null || inventory.getQuantity() < orderItem.getQuantity()) {
                                throw new Exception(
                                                "Insufficient stock for product: " + orderItem.getProduct().getName());
                        }
                }
        }

        private void deductInventoryForOrder(Order order, Branch branch) {
                for (var orderItem : order.getItems()) {
                        Inventory inventory = inventoryRepository.findByProductIdAndBranchId(
                                        orderItem.getProduct().getId(), branch.getId());

                        if (inventory != null) {
                                inventory.setQuantity(inventory.getQuantity() - orderItem.getQuantity());
                                inventoryRepository.save(inventory);
                        }
                }
        }

        private void validateStatusTransition(OrderStatus current, OrderStatus newStatus) throws Exception {
                // Define valid transitions
                switch (current) {
                        case PENDING:
                                if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.CANCELLED) {
                                        throw new Exception("Invalid status transition from PENDING to " + newStatus);
                                }
                                break;
                        case CONFIRMED:
                                if (newStatus != OrderStatus.PROCESSING && newStatus != OrderStatus.CANCELLED) {
                                        throw new Exception("Invalid status transition from CONFIRMED to " + newStatus);
                                }
                                break;
                        case PROCESSING:
                                if (newStatus != OrderStatus.READY_FOR_PICKUP &&
                                                newStatus != OrderStatus.OUT_FOR_DELIVERY &&
                                                newStatus != OrderStatus.CANCELLED) {
                                        throw new Exception(
                                                        "Invalid status transition from PROCESSING to " + newStatus);
                                }
                                break;
                        case READY_FOR_PICKUP:
                        case OUT_FOR_DELIVERY:
                                if (newStatus != OrderStatus.DELIVERED && newStatus != OrderStatus.CANCELLED
                                                && newStatus != OrderStatus.COMPLETED) {
                                        throw new Exception("Invalid status transition from " + current + " to "
                                                        + newStatus);
                                }
                                break;
                        case DELIVERED:
                                if (newStatus != OrderStatus.COMPLETED) {
                                        throw new Exception("Invalid status transition from DELIVERED to " + newStatus);
                                }
                                break;
                        case COMPLETED:
                        case CANCELLED:
                        case REFUNDED:
                                throw new Exception("Cannot change status of " + current + " order");
                }
        }

        /**
         * Validates that the current user has access to a specific branch.
         * Branch-level users can only access their assigned branch.
         */
        private void validateBranchAccess(User user, Long branchId) throws UserException {
                BranchAccessValidator.validateBranchAccess(user, branchId);
        }

}
