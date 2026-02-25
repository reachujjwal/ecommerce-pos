package com.ujjwal.repository;

import com.ujjwal.modal.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {

    /**
     * Find all addresses for a customer
     */
    List<CustomerAddress> findByCustomerId(Long customerId);

    /**
     * Find default address for a customer
     */
    Optional<CustomerAddress> findByCustomerIdAndIsDefaultTrue(Long customerId);

    /**
     * Find specific address for a customer
     */
    Optional<CustomerAddress> findByIdAndCustomerId(Long id, Long customerId);

    /**
     * Unset all default addresses for a customer (before setting a new one)
     */
    @Modifying
    @Query("UPDATE CustomerAddress ca SET ca.isDefault = false WHERE ca.customer.id = :customerId")
    void unsetAllDefaultAddresses(@Param("customerId") Long customerId);

    /**
     * Count addresses for a customer
     */
    long countByCustomerId(Long customerId);

    /**
     * Check if customer has any addresses
     */
    @Query("SELECT CASE WHEN COUNT(ca) > 0 THEN true ELSE false END FROM CustomerAddress ca WHERE ca.customer.id = :customerId")
    boolean hasAddresses(@Param("customerId") Long customerId);
}
