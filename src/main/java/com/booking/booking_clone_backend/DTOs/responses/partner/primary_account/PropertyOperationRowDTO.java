package com.booking.booking_clone_backend.DTOs.responses.partner.primary_account;

import com.booking.booking_clone_backend.DTOs.responses.property.AddressDTO;
import com.booking.booking_clone_backend.models.property.PropertyStatus;

import java.util.UUID;

public record PropertyOperationRowDTO(
        UUID id,
        String name,
        AddressDTO address,
        PropertyStatus status,
        Long arrivalsNext48,
        Long departuresNext48,
        Long guestMessages,
        Long bookingMessages
) {
}
