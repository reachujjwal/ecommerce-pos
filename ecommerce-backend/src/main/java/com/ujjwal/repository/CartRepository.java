package com.ujjwal.repository;

import com.ujjwal.modal.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Find active cart for a customer
     */
    Optional<Cart> findByCustomerId(Long customerId);

    /**
     * Find cart that hasn't expired
     */
    @Query("SELECT c FROM Cart c WHERE c.customer.id = :customerId AND c.expiresAt > :now")
    Optional<Cart> findActiveCartByCustomerId(@Param("customerId") Long customerId, @Param("now") LocalDateTime now);

    /**
     * Delete expired carts (cleanup job)
     */
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.expiresAt < :cutoff")
    void deleteExpiredCarts(@Param("cutoff") LocalDateTime cutoff);

    /**
     * Check if customer has an active cart
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cart c WHERE c.customer.id = :customerId AND c.expiresAt > :now")
    boolean hasActiveCart(@Param("customerId") Long customerId, @Param("now") LocalDateTime now);
}
