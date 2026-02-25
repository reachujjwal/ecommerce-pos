package com.ujjwal.controller;

import com.ujjwal.payload.dto.AddToCartRequest;
import com.ujjwal.payload.dto.CartDTO;
import com.ujjwal.service.CartService;
import com.ujjwal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final com.ujjwal.repository.CustomerRepository customerRepository;
    private final com.ujjwal.repository.UserRepository userRepository;

    /**
     * Get current user's cart
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<CartDTO> getCart(@RequestHeader("Authorization") String jwt) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        CartDTO cart = cartService.getCart(customerId);
        return ResponseEntity.ok(cart);
    }

    // ... existing methods ...

    // Helper method to get customer ID from JWT
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

    /**
     * Add item to cart
     */
    @PostMapping("/items")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<CartDTO> addItem(
            @RequestHeader("Authorization") String jwt,
            @Valid @RequestBody AddToCartRequest request) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        CartDTO cart = cartService.addItem(customerId, request);
        return ResponseEntity.ok(cart);
    }

    /**
     * Update item quantity
     */
    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<CartDTO> updateQuantity(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        CartDTO cart = cartService.updateQuantity(customerId, itemId, quantity);
        return ResponseEntity.ok(cart);
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<CartDTO> removeItem(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long itemId) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        CartDTO cart = cartService.removeItem(customerId, itemId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Clear entire cart
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<Void> clearCart(@RequestHeader("Authorization") String jwt) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }

    // Helper method to get customer ID from JWT
    // private Long getUserCustomerId(String jwt) throws Exception {
    // var user = userService.getUserFromJwtToken(jwt);

    // if (user.getCustomer() == null) {
    // // Self-healing: Create Customer if missing for ROLE_CUSTOMER
    // if (user.getRole().name().equals("ROLE_CUSTOMER")) {
    // com.ujjwal.modal.Customer customer = new com.ujjwal.modal.Customer();
    // customer.setFullName(user.getFullName());
    // customer.setEmail(user.getEmail());
    // customer.setPhone(user.getPhone());
    // customer.setCreatedAt(java.time.LocalDateTime.now());
    // customer.setUpdatedAt(java.time.LocalDateTime.now());

    // customer = customerRepository.save(customer);
    // user.setCustomer(customer);
    // userRepository.save(user);

    // return customer.getId();
    // }
    // throw new Exception("User is not associated with a customer account");
    // }

    // return user.getCustomer().getId();
    // }
}
