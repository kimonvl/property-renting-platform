package gr.aueb.cf.property_renting_platform.DTOs.requests.guest.property;

import gr.aueb.cf.property_renting_platform.models.property.PropertyType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PropertySearchRequest(

        @NotBlank(message = "{NotBlank.propertySearchRequest.city}")
        @Size(min = 2, max = 120, message = "{Size.propertySearchRequest.city}")
        @Pattern(regexp = "^[^\\p{Cntrl}]*$", message = "{Pattern.propertySearchRequest.city}")
        String city,

        PropertyType type,

        @NotNull(message = "{NotNull.propertySearchRequest.minPrice}")
        @DecimalMin(value = "0.0", message = "{DecimalMin.propertySearchRequest.minPrice}")
        @Digits(integer = 10, fraction = 2, message = "{Digits.propertySearchRequest.minPrice}")
        BigDecimal minPrice,

        @NotNull(message = "{NotNull.propertySearchRequest.maxPrice}")
        @DecimalMin(value = "0.0", message = "{DecimalMin.propertySearchRequest.maxPrice}")
        @Digits(integer = 10, fraction = 2, message = "{Digits.propertySearchRequest.maxPrice}")
        BigDecimal maxPrice,

        @NotNull(message = "{NotNull.propertySearchRequest.maxGuest}")
        @Min(value = 1, message = "{Min.propertySearchRequest.maxGuest}")
        @Max(value = 50, message = "{Max.propertySearchRequest.maxGuest}")
        Integer maxGuest,

        Integer bathroomCount,

        Integer bedroomCount,

        @NotNull(message = "{NotNull.propertySearchRequest.checkIn}")
        @FutureOrPresent(message = "{FutureOrPresent.propertySearchRequest.checkIn}")
        LocalDate checkIn,

        @NotNull(message = "{NotNull.propertySearchRequest.checkOut}")
        @FutureOrPresent(message = "{FutureOrPresent.propertySearchRequest.checkOut}")
        LocalDate checkOut,

        List<
                @NotBlank(message = "{NotBlank.propertySearchRequest.amenities}")
                @Size(max = 50, message = "{Size.propertySearchRequest.amenityCode}")
                @Pattern(regexp = "^[A-Za-z0-9_\\-]+$", message = "{Pattern.propertySearchRequest.amenityCode}")
                        String
                > amenities,

        Boolean pets,

        @NotNull(message = "{NotNull.propertySearchRequest.page}")
        @Min(value = 0, message = "{Min.propertySearchRequest.page}")
        Integer page,

        @NotNull(message = "{NotNull.propertySearchRequest.size}")
        @Min(value = 1, message = "{Min.propertySearchRequest.size}")
        @Max(value = 100, message = "{Max.propertySearchRequest.size}")
        Integer size
) {
}