package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.property_renting_platform.models.booking.Booking;

import java.util.List;

public interface PropertyAvailabilityService {
    void blockDatesForBooking(Booking booking) throws EntityInvalidArgumentException;
    int deleteBlocksByBookingIds(List<Long> bookingIds);
}
