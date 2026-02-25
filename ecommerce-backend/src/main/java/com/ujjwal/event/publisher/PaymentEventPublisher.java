package com.ujjwal.event.publisher;

import com.ujjwal.event.PaymentFailedEvent;
import com.ujjwal.event.PaymentInitiatedEvent;
import com.ujjwal.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Publisher for payment-related domain events.
 * Uses Spring's ApplicationEventPublisher to broadcast events to listeners.
 */
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Publish a payment initiated event.
     * All registered listeners will receive this event asynchronously.
     *
     * @param event The payment initiated event to publish
     */
    public void publishPaymentInitiated(PaymentInitiatedEvent event) {

        applicationEventPublisher.publishEvent(event);

        log.debug("PaymentInitiatedEvent published successfully for payment ID: {}",
                event.getPaymentId());
    }

    /**
     * Publish a payment success event.
     * All registered listeners will receive this event asynchronously.
     *
     * @param event The payment success event to publish
     */
    public void publishPaymentSuccess(PaymentSuccessEvent event) {

        applicationEventPublisher.publishEvent(event);

        log.debug("PaymentSuccessEvent published successfully for payment ID: {}",
                event.getPaymentId());
    }

    /**
     * Publish a payment failed event.
     * All registered listeners will receive this event asynchronously.
     *
     * @param event The payment failed event to publish
     */
    public void publishPaymentFailed(PaymentFailedEvent event) {

        applicationEventPublisher.publishEvent(event);

        log.debug("PaymentFailedEvent published successfully for payment ID: {}",
                event.getPaymentId());
    }
}
