package com.booking.booking_clone_backend.services;

import com.booking.booking_clone_backend.DTOs.requests.booking.CreateBookingRequest;
import com.booking.booking_clone_backend.DTOs.responses.booking.CreatePaymentIntentResponse;
import com.booking.booking_clone_backend.exceptions.EntityInvalidArgumentException;
import com.booking.booking_clone_backend.exceptions.EntityNotFoundException;
import com.booking.booking_clone_backend.exceptions.InternalErrorException;
import com.stripe.exception.StripeException;

import java.util.UUID;

public interface StripePaymentService {
    public String createPaymentIntent(UUID bookingId, String email) throws  EntityInvalidArgumentException, EntityNotFoundException, InternalErrorException;
}
