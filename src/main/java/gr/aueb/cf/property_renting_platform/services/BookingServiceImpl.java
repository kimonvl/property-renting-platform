package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.DTOs.requests.booking.CreateBookingRequest;
import gr.aueb.cf.property_renting_platform.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;
import gr.aueb.cf.property_renting_platform.mappers.BookingCheckoutDetailsMapper;
import gr.aueb.cf.property_renting_platform.mappers.BookingCustomMapper;
import gr.aueb.cf.property_renting_platform.models.booking.Booking;
import gr.aueb.cf.property_renting_platform.models.booking.BookingCheckoutDetails;
import gr.aueb.cf.property_renting_platform.models.property.Property;
import gr.aueb.cf.property_renting_platform.models.user.User;
import gr.aueb.cf.property_renting_platform.repos.BookingRepo;
import gr.aueb.cf.property_renting_platform.repos.PropertyRepo;
import gr.aueb.cf.property_renting_platform.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final PropertyRepo propertyRepo;
    private final PropertyAvailabilityService propertyAvailabilityService;
    private final BookingCustomMapper bookingCustomMapper;
    private final BookingCheckoutDetailsMapper bookingCheckoutDetailsMapper;

    @Override
    @PreAuthorize("hasAnyAuthority('CREATE_BOOKING')")
    @Transactional(rollbackFor = {EntityNotFoundException.class, EntityInvalidArgumentException.class})
    public UUID createBooking(CreateBookingRequest request, String userEmail) throws EntityNotFoundException, EntityInvalidArgumentException {
        try {
            User user = userRepo.findByEmailIgnoreCase(userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("CreateBookingUser", "user email not found. userEmail=" + userEmail));

            Property property = propertyRepo.findByUuid(request.propertyId())
                    .orElseThrow(() -> new EntityNotFoundException("CreateBookingProperty", "property not found. propertyId=" + request.propertyId()));

            Integer maxGuests = property.getMaxGuests();
            Integer guestCount = request.guestCount();
            if (maxGuests != null && guestCount != null && guestCount > maxGuests) {
                throw new EntityInvalidArgumentException("CreateBookingGuestCount", "guestCount exceeds maxGuests. propertyId=" + request.propertyId() + ", guestCount=" + guestCount + ", maxGuests=" + maxGuests);
            }

            if (property.getPricePerNight() == null) {
                throw new EntityInvalidArgumentException("CreateBookingPrice", "property price per night is not set. propertyId=" + request);
            }

            long nightsLong = ChronoUnit.DAYS.between(request.checkIn(), request.checkOut());
            if (nightsLong <= 0) {
                throw new EntityInvalidArgumentException("CreateBookingDates", "check-out date must be after check-in date. propertyId=" + request.propertyId() + ", checkIn=" + request.checkIn() + ", checkOut=" + request.checkOut());
            }

            BigDecimal total = BigDecimal.valueOf(nightsLong).multiply(property.getPricePerNight());

            BookingCheckoutDetails details = bookingCheckoutDetailsMapper.toEntity(request.checkOutDetails());
            Booking booking = bookingCustomMapper.createBookingRequestToBooking(
                    request, property, user, details, total
            );
            booking.setHoldExpiresAt(Instant.now().plusSeconds(60));

            Booking savedBooking = bookingRepo.save(booking);

            // check availability and block dates for the booking
            propertyAvailabilityService.blockDatesForBooking(savedBooking);

            log.info("Booking created successfully. propertyId={}, bookingId={}", request.propertyId(), savedBooking.getId());
            return savedBooking.getUuid();

        } catch (EntityNotFoundException | EntityInvalidArgumentException e) {
            log.warn("Failed to create booking. propertyId={}, userEmail={}. Message: {}",request.propertyId(), userEmail, e.getMessage());
            throw e;
        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('DELETE_BOOKING')")
    @Transactional(rollbackFor = {EntityNotFoundException.class})
    public void deleteBooking(Long bookingId) throws EntityNotFoundException {
        try {
            Booking booking = bookingRepo.findById(bookingId)
                    .orElseThrow(() -> new EntityNotFoundException("DeleteBooking", "Failed to delete booking: booking not found. bookingId=" + bookingId));
            bookingRepo.delete(booking);
            log.info("Booking deleted. bookingId={}", bookingId);
        } catch (EntityNotFoundException e) {
            log.warn("Booking deletion failed: not found. bookingId={}", bookingId);
            throw e;
        }
    }
}