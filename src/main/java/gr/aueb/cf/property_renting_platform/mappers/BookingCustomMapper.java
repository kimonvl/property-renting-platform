package gr.aueb.cf.property_renting_platform.mappers;

import gr.aueb.cf.property_renting_platform.DTOs.requests.booking.CreateBookingRequest;
import gr.aueb.cf.property_renting_platform.models.booking.Booking;
import gr.aueb.cf.property_renting_platform.models.booking.BookingCheckoutDetails;
import gr.aueb.cf.property_renting_platform.models.booking.BookingStatus;
import gr.aueb.cf.property_renting_platform.models.booking.PaymentStatus;
import gr.aueb.cf.property_renting_platform.models.property.Property;
import gr.aueb.cf.property_renting_platform.models.user.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BookingCustomMapper {
    public Booking createBookingRequestToBooking(
            CreateBookingRequest request,
            Property property,
            User user,
            BookingCheckoutDetails details,
            BigDecimal total
    ) {
        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setGuest(user);
        booking.setCheckInDate(request.checkIn());
        booking.setCheckOutDate(request.checkOut());
        booking.setGuestCount(request.guestCount());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.REQUIRES_PAYMENT);
        booking.setAmountTotal(total);
        booking.setCheckoutDetails(details);
        return booking;
    }
}
