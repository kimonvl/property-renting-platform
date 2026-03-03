package com.booking.booking_clone_backend.DTOs.responses.booking;

import java.util.UUID;

public record CreatePaymentIntentResponse(
        UUID bookingId,
        String clientSecret
) {}
