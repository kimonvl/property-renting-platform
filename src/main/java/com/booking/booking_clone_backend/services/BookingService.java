package com.booking.booking_clone_backend.services;

import com.booking.booking_clone_backend.DTOs.requests.booking.CreateBookingRequest;
import com.booking.booking_clone_backend.exceptions.EntityInvalidArgumentException;
import com.booking.booking_clone_backend.exceptions.EntityNotFoundException;

import java.util.UUID;

public interface BookingService {
    UUID createBooking(CreateBookingRequest request, String userEmail) throws EntityNotFoundException, EntityInvalidArgumentException;
    void deleteBooking(Long bookingId) throws EntityNotFoundException;
}
