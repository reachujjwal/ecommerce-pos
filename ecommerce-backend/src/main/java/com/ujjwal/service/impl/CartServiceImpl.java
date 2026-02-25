package com.ujjwal.service.impl;

import com.ujjwal.exception.ResourceNotFoundException;
import com.ujjwal.modal.Cart;
import com.ujjwal.modal.CartItem;
import com.ujjwal.modal.Customer;
import com.ujjwal.modal.Product;
import com.ujjwal.payload.dto.AddToCartRequest;
import com.ujjwal.payload.dto.CartDTO;
import com.ujjwal.payload.dto.CartItemDTO;
import com.ujjwal.repository.CartItemRepository;
import com.ujjwal.repository.CartRepository;
import com.ujjwal.repository.CustomerRepository;
import com.ujjwal.repository.ProductRepository;
import com.ujjwal.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CartDTO getOrCreateCart(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Find active cart (not expired)
        Optional<Cart> existingCart = cartRepository.findActiveCartByCustomerId(customerId, LocalDateTime.now());

        Cart cart;
        if (existingCart.isPresent()) {
            cart = existingCart.get();
        } else {
            // Create new cart
            cart = Cart.builder()
                    .customer(customer)
                    .build();
            cart = cartRepository.save(cart);
        }

        return mapToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO addItem(Long customerId, AddToCartRequest request) throws Exception {
        // Get or create cart
        Cart cart = getOrCreateCartEntity(customerId);

        // Validate product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(
                cart.getId(), product.getId());

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            // Add new item
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .priceSnapshot(product.getSellingPrice())
                    .build();
            cartItemRepository.save(newItem);
        }

        // Refresh cart
        cart = cartRepository.findById(cart.getId()).orElseThrow();
        return mapToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO removeItem(Long customerId, Long cartItemId) throws Exception {
        Cart cart = getOrCreateCartEntity(customerId);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // Verify item belongs to customer's cart
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new Exception("Cart item does not belong to this customer");
        }

        cartItemRepository.delete(item);

        // Refresh cart
        cart = cartRepository.findById(cart.getId()).orElseThrow();
        return mapToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO updateQuantity(Long customerId, Long cartItemId, Integer quantity) throws Exception {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        Cart cart = getOrCreateCartEntity(customerId);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // Verify item belongs to customer's cart
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new Exception("Cart item does not belong to this customer");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        // Refresh cart
        cart = cartRepository.findById(cart.getId()).orElseThrow();
        return mapToDTO(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long customerId) throws Exception {
        Cart cart = getOrCreateCartEntity(customerId);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCart(Long customerId) throws Exception {
        Cart cart = getOrCreateCartEntity(customerId);
        return mapToDTO(cart);
    }

    // Helper methods

    private Cart getOrCreateCartEntity(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Optional<Cart> existingCart = cartRepository.findActiveCartByCustomerId(customerId, LocalDateTime.now());

        if (existingCart.isPresent()) {
            return existingCart.get();
        } else {
            Cart cart = Cart.builder()
                    .customer(customer)
                    .build();
            return cartRepository.save(cart);
        }
    }

    private CartDTO mapToDTO(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(this::mapItemToDTO)
                .collect(Collectors.toList());

        return CartDTO.builder()
                .cartId(cart.getId())
                .customerId(cart.getCustomer().getId())
                .items(itemDTOs)
                .subtotal(cart.getSubtotal())
                .itemCount(cart.getItemCount())
                .createdAt(cart.getCreatedAt())
                .expiresAt(cart.getExpiresAt())
                .build();
    }

    private CartItemDTO mapItemToDTO(CartItem item) {
        return CartItemDTO.builder()
                .itemId(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImage(item.getProduct().getImage())
                .sku(item.getProduct().getSku())
                .quantity(item.getQuantity())
                .price(item.getPriceSnapshot())
                .total(item.getTotal())
                .addedAt(item.getAddedAt())
                .build();
    }
}
