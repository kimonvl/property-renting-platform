package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.exceptions.InternalErrorException;
import gr.aueb.cf.property_renting_platform.models.booking.BookingStatus;
import gr.aueb.cf.property_renting_platform.models.booking.PaymentStatus;
import gr.aueb.cf.property_renting_platform.repos.BookingRepo;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeWebhookServiceImpl implements StripeWebhookService{

    private final PropertyAvailabilityService propertyAvailabilityService;
    private final BookingRepo bookingRepo;

    @Override
    @Transactional(rollbackFor = {InternalErrorException.class})
    public void handleEvent(Event event) throws InternalErrorException {
        switch (event.getType()) {
            case "payment_intent.succeeded" -> handlePaymentSucceeded(event);
            case "payment_intent.payment_failed" -> handlePaymentFailed(event);
            default -> {
                // ignore
            }
        }
    }


    private void handlePaymentSucceeded(Event event) throws InternalErrorException {
        StripeObject stripeObject = null;
        try {
            stripeObject = event.getDataObjectDeserializer().deserializeUnsafe();

            if (!(stripeObject instanceof PaymentIntent intent)) {
                log.error("Unexpected Stripe object type in handlePaymentSuccess. eventId={}, eventType={}, objectType={}", event.getId(), event.getType(), stripeObject.getClass().getName());
                throw new InternalErrorException("HandlePaymentSuccessObjectType", "Unexpected Stripe object type in handlePaymentSuccess. eventId=" + event.getId() + ", eventType=" + event.getType() + ", objectType=" + stripeObject.getClass().getName());
            }


            bookingRepo.findByPaymentIntentId(intent.getId())
                    .ifPresent(booking -> {
                        if (booking.getPaymentStatus() == PaymentStatus.SUCCEEDED) return;

                        booking.setPaymentStatus(PaymentStatus.SUCCEEDED);
                        booking.setStatus(BookingStatus.CONFIRMED);
                        booking.setPaidAt(Instant.now());
                        bookingRepo.save(booking);
                        log.info("Booking with id={} has been confirmed", booking.getId());
                    });
        } catch (EventDataObjectDeserializationException e) {
            log.error("Failed to deserialize event data object in handlePaymentSuccess. eventId={}, eventType={}", event.getId(), event.getType(), e);
            throw new InternalErrorException("HandlePaymentSuccessDeserialization", "Failed to deserialize event data object for event with id=" + event.getId() + " and type=" + event.getType());
        }
    }

    private void handlePaymentFailed(Event event) throws InternalErrorException {
        StripeObject stripeObject = null;
        try {
            stripeObject = event.getDataObjectDeserializer().deserializeUnsafe();

            if (!(stripeObject instanceof PaymentIntent intent)) {
                log.error("Unexpected Stripe object type in handlePaymentFailed. eventId={}, eventType={}, objectType={}", event.getId(), event.getType(), stripeObject.getClass().getName());
                throw new InternalErrorException("HandlePaymentFailedObjectType", "Unexpected Stripe object type in handlePaymentSuccess. eventId=" + event.getId() + ", eventType=" + event.getType() + ", objectType=" + stripeObject.getClass().getName());
            }

            bookingRepo.findByPaymentIntentId(intent.getId())
                    .ifPresent(booking -> {
                        if (booking.getPaymentStatus() == PaymentStatus.SUCCEEDED) return;
                        booking.setPaymentStatus(PaymentStatus.FAILED);
                        booking.setStatus(BookingStatus.CANCELLED);
                        propertyAvailabilityService.deleteBlocksByBookingIds(List.of(booking.getId()));
                        bookingRepo.save(booking);
                    });
        } catch (EventDataObjectDeserializationException e) {
            log.error("Failed to deserialize event data object in handlePaymentFailed. eventId={}, eventType={}", event.getId(), event.getType(), e);
            throw new InternalErrorException("HandlePaymentFailedDeserialization", "Failed to deserialize event data object for event with id=" + event.getId() + " and type=" + event.getType());
        }
    }
}
