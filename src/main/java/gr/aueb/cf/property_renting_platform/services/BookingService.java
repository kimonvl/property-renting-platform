package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.DTOs.requests.booking.CreateBookingRequest;
import gr.aueb.cf.property_renting_platform.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;

import java.util.UUID;

public interface BookingService {
    UUID createBooking(CreateBookingRequest request, String userEmail) throws EntityNotFoundException, EntityInvalidArgumentException;
    void deleteBooking(Long bookingId) throws EntityNotFoundException;
}
