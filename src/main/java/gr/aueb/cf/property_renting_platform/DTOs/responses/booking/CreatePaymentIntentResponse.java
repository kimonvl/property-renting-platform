package gr.aueb.cf.property_renting_platform.DTOs.responses.booking;

import java.util.UUID;

public record CreatePaymentIntentResponse(
        UUID bookingId,
        String clientSecret
) {}
