package com.booking.booking_clone_backend.DTOs.responses.booking;

import com.booking.booking_clone_backend.models.booking.BookingStatus;
import com.booking.booking_clone_backend.models.booking.PaymentStatus;

import java.util.UUID;

public record BookingStatusResponse(
        UUID id,
        BookingStatus status,
        PaymentStatus paymentStatus
) {}
