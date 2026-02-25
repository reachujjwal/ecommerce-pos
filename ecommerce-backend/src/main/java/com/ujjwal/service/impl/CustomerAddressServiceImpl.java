package com.ujjwal.service.impl;

import com.ujjwal.exception.ResourceNotFoundException;
import com.ujjwal.modal.Customer;
import com.ujjwal.modal.CustomerAddress;
import com.ujjwal.payload.dto.CustomerAddressDTO;
import com.ujjwal.repository.CustomerAddressRepository;
import com.ujjwal.repository.CustomerRepository;
import com.ujjwal.service.CustomerAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerAddressServiceImpl implements CustomerAddressService {

    private final CustomerAddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerAddressDTO createAddress(Long customerId, CustomerAddressDTO dto) throws Exception {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // If this is the first address or marked as default, set it as default
        boolean isFirstAddress = !addressRepository.hasAddresses(customerId);
        boolean shouldBeDefault = isFirstAddress || (dto.getIsDefault() != null && dto.getIsDefault());

        if (shouldBeDefault) {
            // Unset all existing default addresses
            addressRepository.unsetAllDefaultAddresses(customerId);
        }

        CustomerAddress address = CustomerAddress.builder()
                .customer(customer)
                .fullName(dto.getFullName())
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .zipCode(dto.getZipCode())
                .phone(dto.getPhone())
                .isDefault(shouldBeDefault)
                .build();

        address = addressRepository.save(address);
        return mapToDTO(address);
    }

    @Override
    @Transactional
    public CustomerAddressDTO updateAddress(Long customerId, Long addressId, CustomerAddressDTO dto) throws Exception {
        CustomerAddress address = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        address.setFullName(dto.getFullName());
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setZipCode(dto.getZipCode());
        address.setPhone(dto.getPhone());

        // Handle default flag
        if (dto.getIsDefault() != null && dto.getIsDefault() && !address.getIsDefault()) {
            addressRepository.unsetAllDefaultAddresses(customerId);
            address.setIsDefault(true);
        }

        address = addressRepository.save(address);
        return mapToDTO(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long customerId, Long addressId) throws Exception {
        CustomerAddress address = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        boolean wasDefault = address.getIsDefault();
        addressRepository.delete(address);

        // If deleted address was default, set another address as default
        if (wasDefault) {
            List<CustomerAddress> remainingAddresses = addressRepository.findByCustomerId(customerId);
            if (!remainingAddresses.isEmpty()) {
                CustomerAddress newDefault = remainingAddresses.get(0);
                newDefault.setIsDefault(true);
                addressRepository.save(newDefault);
            }
        }
    }

    @Override
    @Transactional
    public CustomerAddressDTO setDefaultAddress(Long customerId, Long addressId) throws Exception {
        CustomerAddress address = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        // Unset all default addresses
        addressRepository.unsetAllDefaultAddresses(customerId);

        // Set this address as default
        address.setIsDefault(true);
        address = addressRepository.save(address);

        return mapToDTO(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerAddressDTO> getCustomerAddresses(Long customerId) {
        return addressRepository.findByCustomerId(customerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerAddressDTO getDefaultAddress(Long customerId) throws Exception {
        CustomerAddress address = addressRepository.findByCustomerIdAndIsDefaultTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("No default address found"));
        return mapToDTO(address);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerAddressDTO getAddress(Long customerId, Long addressId) throws Exception {
        CustomerAddress address = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        return mapToDTO(address);
    }

    // Helper method
    private CustomerAddressDTO mapToDTO(CustomerAddress address) {
        return CustomerAddressDTO.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .phone(address.getPhone())
                .isDefault(address.getIsDefault())
                .fullAddress(address.getFullAddress())
                .build();
    }
}
