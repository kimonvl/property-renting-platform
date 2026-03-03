package com.booking.booking_clone_backend.DTOs.responses.property;

import com.booking.booking_clone_backend.DTOs.responses.dictionaries.amenity.AmenityDTO;
import com.booking.booking_clone_backend.DTOs.responses.review.ReviewSummaryDTO;
import com.booking.booking_clone_backend.models.property.CurrencyCode;
import com.booking.booking_clone_backend.models.property.PetsPolicy;
import com.booking.booking_clone_backend.models.property.PropertyType;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record PropertyDetailsDTO(
        UUID id,
        Set<AmenityDTO> propertyAmenities,
        AddressDTO address,
        PropertyType type,
        String name,
        BigDecimal pricePerNight,
        CurrencyCode currency,
        BigDecimal sizeSqm,
        Integer maxGuests,
        Integer bathrooms,
        Integer livingRoomCount,
        Integer bedroomCount,
        String bedSummary,
        Set<String> photoUrls,
        String mainPhotoUrl,
        ReviewSummaryDTO reviewSummary,

        String checkInFrom,     // "15:00"
        String checkInUntil,    // "23:30"
        String checkOutFrom,    // "08:00"
        String checkOutUntil,   // "11:00"
        Boolean childrenAllowed,
        Boolean cotsOffered,
        Boolean smokingAllowed,
        Boolean partiesAllowed,
        PetsPolicy petsPolicy
) {
}
