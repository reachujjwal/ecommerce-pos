package com.ujjwal.service;

import com.ujjwal.payload.dto.AddToCartRequest;
import com.ujjwal.payload.dto.CartDTO;

public interface CartService {

    /**
     * Get or create cart for customer
     */
    CartDTO getOrCreateCart(Long customerId);

    /**
     * Add item to cart or update quantity if already exists
     */
    CartDTO addItem(Long customerId, AddToCartRequest request) throws Exception;

    /**
     * Remove item from cart
     */
    CartDTO removeItem(Long customerId, Long cartItemId) throws Exception;

    /**
     * Update item quantity
     */
    CartDTO updateQuantity(Long customerId, Long cartItemId, Integer quantity) throws Exception;

    /**
     * Clear all items from cart
     */
    void clearCart(Long customerId) throws Exception;

    /**
     * Get cart with all items
     */
    CartDTO getCart(Long customerId) throws Exception;
}
