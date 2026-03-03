package com.booking.booking_clone_backend.DTOs.responses.property;

import com.booking.booking_clone_backend.DTOs.responses.dictionaries.amenity.AmenityDTO;
import com.booking.booking_clone_backend.DTOs.responses.dictionaries.language.LanguageDTO;
import com.booking.booking_clone_backend.DTOs.responses.review.ReviewSummaryDTO;
import com.booking.booking_clone_backend.DTOs.responses.user.UserDTO;
import com.booking.booking_clone_backend.models.property.CurrencyCode;
import com.booking.booking_clone_backend.models.property.PropertyStatus;
import com.booking.booking_clone_backend.models.property.PropertyType;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record PropertyShortDTO(
        UUID id,
        Set<AmenityDTO> propertyAmenities,
        AddressDTO address,
        PropertyType type,
        PropertyStatus status,
        String name,
        BigDecimal pricePerNight,
        CurrencyCode currency,
        BigDecimal sizeSqm,
        Integer maxGuests,
        Integer bathrooms,
        Integer livingRoomCount,
        Integer bedroomCount,
        String bedSummary,
        String mainPhotoUrl,
        ReviewSummaryDTO reviewSummary
) {
}
