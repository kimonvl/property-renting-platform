package gr.aueb.cf.property_renting_platform.DTOs.responses.booking;

import gr.aueb.cf.property_renting_platform.models.booking.BookingStatus;
import gr.aueb.cf.property_renting_platform.models.booking.PaymentStatus;

import java.util.UUID;

public record BookingStatusResponse(
        UUID id,
        BookingStatus status,
        PaymentStatus paymentStatus
) {}
