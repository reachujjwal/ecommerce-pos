package com.ujjwal.service;

import com.ujjwal.payload.dto.CustomerAddressDTO;

import java.util.List;

public interface CustomerAddressService {

    /**
     * Create new address for customer
     */
    CustomerAddressDTO createAddress(Long customerId, CustomerAddressDTO dto) throws Exception;

    /**
     * Update existing address
     */
    CustomerAddressDTO updateAddress(Long customerId, Long addressId, CustomerAddressDTO dto) throws Exception;

    /**
     * Delete address
     */
    void deleteAddress(Long customerId, Long addressId) throws Exception;

    /**
     * Set address as default
     */
    CustomerAddressDTO setDefaultAddress(Long customerId, Long addressId) throws Exception;

    /**
     * Get all addresses for customer
     */
    List<CustomerAddressDTO> getCustomerAddresses(Long customerId);

    /**
     * Get default address for customer
     */
    CustomerAddressDTO getDefaultAddress(Long customerId) throws Exception;

    /**
     * Get specific address
     */
    CustomerAddressDTO getAddress(Long customerId, Long addressId) throws Exception;
}
