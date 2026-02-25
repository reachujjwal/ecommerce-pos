package com.ujjwal.event.listener;

import com.ujjwal.domain.PaymentStatus;
import com.ujjwal.domain.SubscriptionStatus;
import com.ujjwal.event.PaymentFailedEvent;
import com.ujjwal.event.PaymentInitiatedEvent;
import com.ujjwal.event.PaymentSuccessEvent;
import com.ujjwal.exception.ResourceNotFoundException;

import com.ujjwal.modal.Subscription;
import com.ujjwal.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listener for payment-related domain events.
 * Handles business logic triggered by payment lifecycle events.
 */
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentEventListener.class);

    private final SubscriptionRepository subscriptionRepository;

    /**
     * Handle payment initiation event.
     * Used for tracking and analytics purposes.
     *
     * @param event The payment initiated event
     */
    @EventListener
    @Async
    public void handlePaymentInitiated(PaymentInitiatedEvent event) {

        // Future enhancements:
        // - Send payment link email to customer
        // - Track payment initiation metrics
        // - Start payment timeout monitoring
    }

    /**
     * Handle payment success event.
     * Updates booking status from PENDING to CONFIRMED.
     *
     * @param event The payment success event
     */
    @EventListener
    @Transactional
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("Payment succeeded - PaymentId: {}, BookingId: {}, Amount: {}, TransactionId: {}",
                event.getPaymentId(),
                event.getSubscriptionId(),
                event.getAmount(),
                event.getTransactionId());

        if (event.getSubscriptionId() != null) {
            try {
                Subscription subscription = subscriptionRepository.findById(event.getSubscriptionId())
                        .orElseThrow(() -> new Exception("subscription not found"));

                // Update booking status to CONFIRMED

                subscription.setStatus(SubscriptionStatus.ACTIVE);
                subscription.setPaymentStatus(PaymentStatus.SUCCESS);
                subscriptionRepository.save(subscription);

            } catch (ResourceNotFoundException e) {
                log.error("Failed to update booking status - Booking not found: {}", event.getSubscriptionId(), e);
            } catch (Exception e) {
                log.error("Failed to update booking status for payment success - BookingId: {}, PaymentId: {}",
                        event.getSubscriptionId(), event.getPaymentId(), e);
            }
        } else {
            log.warn("Payment success event received without booking ID - PaymentId: {}", event.getPaymentId());
        }
    }

    /**
     * Handle payment failure event.
     * Logs failure and can trigger retry logic or notifications.
     *
     * @param event The payment failed event
     */
    @EventListener
    @Async
    public void handlePaymentFailed(PaymentFailedEvent event) {

        if (event.getSubscriptionId() != null) {
            try {
                Subscription subscription = subscriptionRepository.findById(event.getSubscriptionId())
                        .orElseThrow(() -> new Exception("subscription not found"));

                subscription.setPaymentStatus(PaymentStatus.FAILED);
                subscriptionRepository.save(subscription);

            } catch (ResourceNotFoundException e) {
                log.error("Failed to process payment failure - Booking not found: {}", event.getSubscriptionId(), e);
            } catch (Exception e) {
                log.error("Failed to handle payment failure event - BookingId: {}, PaymentId: {}",
                        event.getSubscriptionId(), event.getPaymentId(), e);
            }
        }
    }
}
