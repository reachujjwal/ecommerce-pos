package com.ujjwal.controller;

import com.ujjwal.payload.dto.CustomerAddressDTO;
import com.ujjwal.service.CustomerAddressService;
import com.ujjwal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/addresses")
@RequiredArgsConstructor
public class CustomerAddressController {

    private final CustomerAddressService addressService;
    private final UserService userService;
    private final com.ujjwal.repository.CustomerRepository customerRepository;
    private final com.ujjwal.repository.UserRepository userRepository;

    /**
     * Get all addresses for current customer
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<CustomerAddressDTO>> getAddresses(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        List<CustomerAddressDTO> addresses = addressService.getCustomerAddresses(customerId);
        return ResponseEntity.ok(addresses);
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
     * Get specific address
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerAddressDTO> getAddress(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        CustomerAddressDTO address = addressService.getAddress(customerId, id);
        return ResponseEntity.ok(address);
    }

    /**
     * Get default address
     */
    @GetMapping("/default")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerAddressDTO> getDefaultAddress(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        CustomerAddressDTO address = addressService.getDefaultAddress(customerId);
        return ResponseEntity.ok(address);
    }

    /**
     * Create new address
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerAddressDTO> createAddress(
            @RequestHeader("Authorization") String jwt,
            @Valid @RequestBody CustomerAddressDTO dto) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        CustomerAddressDTO created = addressService.createAddress(customerId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update address
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerAddressDTO> updateAddress(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id,
            @Valid @RequestBody CustomerAddressDTO dto) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        CustomerAddressDTO updated = addressService.updateAddress(customerId, id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Set address as default
     */
    @PutMapping("/{id}/default")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerAddressDTO> setDefaultAddress(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        CustomerAddressDTO updated = addressService.setDefaultAddress(customerId, id);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete address
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<Void> deleteAddress(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id) throws Exception {
        Long customerId = getUserCustomerId(jwt);
        addressService.deleteAddress(customerId, id);
        return ResponseEntity.noContent().build();
    }

    // Helper method to get customer ID from JWT
//    private Long getUserCustomerId(String jwt) throws Exception {
//        var user = userService.getUserFromJwtToken(jwt);
//
//        if (user.getCustomer() == null) {
//            // Self-healing: Create Customer if missing for ROLE_CUSTOMER
//            if (user.getRole().toString().equals("ROLE_CUSTOMER")) {
//                com.ujjwal.modal.Customer customer = new com.ujjwal.modal.Customer();
//                customer.setFullName(user.getFullName());
//                customer.setEmail(user.getEmail());
//                customer.setPhone(user.getPhone());
//                customer.setCreatedAt(java.time.LocalDateTime.now());
//                customer.setUpdatedAt(java.time.LocalDateTime.now());
//
//                customer = customerRepository.save(customer);
//                user.setCustomer(customer);
//                userRepository.save(user);
//
//                return customer.getId();
//            }
//            throw new Exception("User is not associated with a customer account");
//        }
//
//        return user.getCustomer().getId();
//    }
}
