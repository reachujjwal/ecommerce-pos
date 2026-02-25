package com.ujjwal.service;

import com.ujjwal.exception.PaymentException;
import com.ujjwal.exception.UserException;
import com.ujjwal.payload.dto.PaymentDTO;
import com.ujjwal.payload.request.PaymentInitiateRequest;
import com.ujjwal.payload.request.PaymentVerifyRequest;
import com.ujjwal.payload.response.PaymentInitiateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    /**
     * Initiate a new payment (creates order with payment gateway)
     */
    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) throws PaymentException;

    /**
     * Verify payment after gateway callback
     */
    PaymentDTO verifyPayment(PaymentVerifyRequest request) throws PaymentException;


    /**
     * Get all payments (admin)
     */
    Page<PaymentDTO> getAllPayments(Pageable pageable) throws UserException;




}
