package gr.aueb.cf.property_renting_platform.DTOs.responses.property;

import gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.amenity.AmenityDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.review.ReviewSummaryDTO;
import gr.aueb.cf.property_renting_platform.models.property.CurrencyCode;
import gr.aueb.cf.property_renting_platform.models.property.PropertyStatus;
import gr.aueb.cf.property_renting_platform.models.property.PropertyType;

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
