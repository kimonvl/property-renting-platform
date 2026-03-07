package gr.aueb.cf.property_renting_platform.DTOs.responses.partner.primary_account;

import gr.aueb.cf.property_renting_platform.DTOs.responses.property.AddressDTO;
import gr.aueb.cf.property_renting_platform.models.property.PropertyStatus;

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
