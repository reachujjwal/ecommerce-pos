package com.ujjwal.service.impl;

import com.ujjwal.domain.PaymentGateway;
import com.ujjwal.domain.PaymentStatus;
import com.ujjwal.domain.SubscriptionStatus;
import com.ujjwal.exception.PaymentException;
import com.ujjwal.modal.Store;
import com.ujjwal.modal.Subscription;
import com.ujjwal.modal.SubscriptionPlan;
import com.ujjwal.payload.request.PaymentInitiateRequest;
import com.ujjwal.payload.response.PaymentInitiateResponse;
import com.ujjwal.repository.StoreRepository;
import com.ujjwal.repository.SubscriptionPlanRepository;
import com.ujjwal.repository.SubscriptionRepository;
import com.ujjwal.service.PaymentService;
import com.ujjwal.service.SubscriptionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

        private final SubscriptionRepository subscriptionRepository;
        private final StoreRepository storeRepository;

        private final SubscriptionPlanRepository planRepository;
        private final PaymentService paymentService;

        @Override
        @Transactional
        public PaymentInitiateResponse createSubscription(Long storeId,
                        Long planId,
                        PaymentGateway gateway,
                        String transactionId) throws PaymentException {
                Store store = storeRepository.findById(storeId)
                                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

                SubscriptionPlan plan = planRepository.findById(planId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Subscription Plan not found"));

                Subscription sub = Subscription.builder()
                                .store(store)
                                .plan(plan)
                                .startDate(LocalDate.now())
                                .endDate(LocalDate.now().plusMonths(1)) // 🔁 use plan billing cycle in future
                                .status(SubscriptionStatus.ACTIVE)
                                .paymentStatus(PaymentStatus.PENDING)
                                .paymentGateway(gateway)
                                .transactionId(transactionId)
                                .build();
                Subscription savedSub = subscriptionRepository.save(sub);
                PaymentInitiateRequest paymentInitiateRequest = PaymentInitiateRequest.builder()
                                .amount(plan.getPrice())
                                .subscriptionId(savedSub.getId())
                                .description("subscribe " + plan.getName())
                                .storeId(storeId)
                                .gateway(gateway)
                                .build();
                PaymentInitiateResponse payment = paymentService.initiatePayment(paymentInitiateRequest);

                return payment;
        }

        @Override
        public PaymentInitiateResponse upgradeSubscription(Long storeId,
                        Long planId, PaymentGateway gateway,
                        String transactionId) throws PaymentException {
                Store store = storeRepository.findById(storeId)
                                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

                SubscriptionPlan plan = planRepository.findById(planId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Subscription Plan not found"));

                List<Subscription> activeSub = subscriptionRepository.findByStoreAndStatus(store,
                                SubscriptionStatus.ACTIVE);

                for (Subscription sub : activeSub) {
                        sub.setStatus(SubscriptionStatus.CANCELLED);
                        subscriptionRepository.save(sub);
                }

                Subscription sub = Subscription.builder()
                                .store(store)
                                .plan(plan)
                                .startDate(LocalDate.now())
                                .endDate(LocalDate.now().plusMonths(1))
                                .status(SubscriptionStatus.ACTIVE)
                                .paymentStatus(PaymentStatus.SUCCESS)
                                .paymentGateway(gateway)
                                .transactionId(transactionId)
                                .build();
                Subscription updatedSubscription = subscriptionRepository.save(sub);

                PaymentInitiateRequest paymentInitiateRequest = PaymentInitiateRequest.builder()
                                .amount(plan.getPrice())
                                .subscriptionId(updatedSubscription.getId())
                                .description("subscribe " + plan.getName())
                                .storeId(storeId)
                                .gateway(gateway)
                                .build();
                PaymentInitiateResponse payment = paymentService.initiatePayment(paymentInitiateRequest);
                return payment;
        }

        @Override
        public Subscription activateSubscription(Long subscriptionId) {
                Subscription sub = subscriptionRepository.findById(subscriptionId)
                                .orElseThrow(() -> new RuntimeException("Subscription not found"));
                sub.setStatus(SubscriptionStatus.ACTIVE);
                sub.setPaymentStatus(PaymentStatus.SUCCESS);
                return subscriptionRepository.save(sub);
        }

        @Override
        public Subscription cancelSubscription(Long subscriptionId) {
                Subscription sub = subscriptionRepository.findById(subscriptionId)
                                .orElseThrow(() -> new RuntimeException("Subscription not found"));
                sub.setStatus(SubscriptionStatus.CANCELLED);
                return subscriptionRepository.save(sub);
        }

        @Override
        public void expirePastSubscriptions() {
                List<Subscription> all = subscriptionRepository.findAll();
                all.stream()
                                .filter(s -> s.getEndDate().isBefore(LocalDate.now())
                                                && s.getStatus() != SubscriptionStatus.EXPIRED)
                                .forEach(s -> {
                                        s.setStatus(SubscriptionStatus.EXPIRED);
                                        subscriptionRepository.save(s);
                                });
        }

        @Override
        public Subscription updatePaymentStatus(Long subscriptionId, PaymentStatus status) {
                Subscription sub = subscriptionRepository.findById(subscriptionId)
                                .orElseThrow(() -> new RuntimeException("Subscription not found"));
                sub.setPaymentStatus(status);
                return subscriptionRepository.save(sub);
        }

        @Override
        public List<Subscription> getSubscriptionsByStore(
                        Long storeId,
                        SubscriptionStatus status) {
                Store store = storeRepository.findById(storeId).orElseThrow(
                                () -> new EntityNotFoundException("Store not found"));
                if (status != null) {
                        return subscriptionRepository.findByStoreAndStatus(store, status);
                }
                return subscriptionRepository.findByStore(store);
        }

        @Override
        public List<Subscription> getAllSubscriptions(SubscriptionStatus status) {
                if (status != null) {
                        return subscriptionRepository.findByStatus(status);
                }
                return subscriptionRepository.findAll();
        }

        @Override
        public List<Subscription> getExpiringSubscriptionsWithin(int days) {
                LocalDate today = LocalDate.now();
                LocalDate future = today.plusDays(days);
                return subscriptionRepository.findByEndDateBetween(today, future);
        }

        @Override
        public Long countByStatus(SubscriptionStatus status) {
                return subscriptionRepository.countByStatus(status);
        }
}
