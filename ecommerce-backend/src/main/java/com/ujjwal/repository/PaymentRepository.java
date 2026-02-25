package com.ujjwal.repository;

import com.ujjwal.domain.PaymentStatus;
import com.ujjwal.modal.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findBySubscriptionId(Long subscriptionId);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByStatus(PaymentStatus status);

    Page<Payment> findByStoreId(Long storeId, Pageable pageable);



}
