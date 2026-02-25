package com.ujjwal.service;


import com.ujjwal.exception.ResourceNotFoundException;
import com.ujjwal.modal.Customer;

import java.util.List;

public interface CustomerService {

    Customer createCustomer(Customer customer);

    Customer updateCustomer(Long id, Customer customer) throws ResourceNotFoundException;

    void deleteCustomer(Long id) throws ResourceNotFoundException;

    Customer getCustomerById(Long id) throws ResourceNotFoundException;

    List<Customer> getAllCustomers();

    List<Customer> searchCustomer(String keyword);

}

