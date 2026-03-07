package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;
import gr.aueb.cf.property_renting_platform.exceptions.InternalErrorException;
import gr.aueb.cf.property_renting_platform.models.booking.Booking;

import gr.aueb.cf.property_renting_platform.models.booking.PaymentStatus;
import gr.aueb.cf.property_renting_platform.models.user.User;
import gr.aueb.cf.property_renting_platform.repos.BookingRepo;

import gr.aueb.cf.property_renting_platform.repos.UserRepo;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentServiceImpl implements StripePaymentService{
    private final UserRepo userRepo;
    private final BookingRepo bookingRepo;

    @Override
    @PreAuthorize("hasAuthority('CREATE_PAYMENT')")
    @Transactional(
            noRollbackFor = {InternalErrorException.class},
            rollbackFor = {EntityNotFoundException.class, EntityInvalidArgumentException.class})
    public String createPaymentIntent(UUID bookingId, String email) throws EntityInvalidArgumentException, EntityNotFoundException, InternalErrorException {
        Booking booking = null;
        try {
            booking = getBooking(bookingId, email);

            PaymentIntent intent = getPaymentIntent(booking);

            booking.setPaymentIntentId(intent.getId());
            booking.setPaymentStatus(PaymentStatus.PROCESSING);
            bookingRepo.save(booking);

            log.info("Payment intent created for booking with id={} and guest with email={}", booking, email);
            return intent.getClientSecret();
        } catch (StripeException e) {
            booking.setPaymentStatus(PaymentStatus.FAILED);
            bookingRepo.save(booking);
            log.error("Stripe create payment intent failed. bookingId={}, email={}", bookingId, email, e);
            throw new InternalErrorException(
                    "CreatePaymentStripe",
                    "Payment provider error while creating payment intent."
            );
        } catch (EntityNotFoundException | EntityInvalidArgumentException e) {
            log.warn("Create payment intent failed for booking with id={} and guest with email={}. Reason: {}",
                    bookingId, email, e.getMessage());
            throw e;
        }
    }


    private Booking getBooking(UUID bookingId, String email) throws EntityNotFoundException, EntityInvalidArgumentException {
        Booking booking;
        booking = bookingRepo.findByUuid(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("CreatePaymentBooking", "Booking with id=" + bookingId + " not found"));
        User user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("CreatePaymentUser", "User with email=" + email + " not found"));

        // Check if user that issues the payment request is the same as the user that created the booking
        if (!Objects.equals(user, booking.getGuest())) {
            throw new EntityInvalidArgumentException("CreatePaymentUser", "User with email=" + email + " is not the guest of the booking with id=" + bookingId);
        }

        if (booking.getPaymentStatus() != PaymentStatus.REQUIRES_PAYMENT) {
            throw new EntityInvalidArgumentException("CreatePaymentBookingStatus", "Booking with id=" + bookingId + " has invalid payment status for creating payment intent. Current payment status=" + booking.getPaymentStatus());
        }

        if (booking.getHoldExpiresAt() != null && booking.getHoldExpiresAt().isBefore(Instant.now())) {
            throw new EntityInvalidArgumentException("CreatePaymentBookingExpired", "Booking with id=" + bookingId + " has expired hold and cannot create payment intent. Hold expired at=" + booking.getHoldExpiresAt());
        }
        return booking;
    }

    private static PaymentIntent getPaymentIntent(Booking booking) throws StripeException {
        long amountInCents = booking.getAmountTotal()
                .movePointRight(2)      // 12345
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(booking.getProperty().getCurrency().name().toLowerCase()) // e.g. "eur"
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .putMetadata("bookingId", String.valueOf(booking.getId()))
                .build();

        return PaymentIntent.create(params);
    }
}
