package gr.aueb.cf.property_renting_platform.DTOs.requests.partner.apartment;

import gr.aueb.cf.property_renting_platform.DTOs.responses.property.AddressDTO;
import gr.aueb.cf.property_renting_platform.models.property.ParkingPolicy;
import gr.aueb.cf.property_renting_platform.models.property.PetsPolicy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public record CreatePropertyRequest(
        @NotBlank(message = "{NotBlank.createApartmentRequest.propertyName}")
        @Size(min = 3, max = 200, message = "{Size.createApartmentRequest.propertyName}")

        @Pattern(
                regexp = "^(?=(?:.*\\p{L}){3,})[^\\p{Cntrl}]+$",
                message = "{Pattern.createApartmentRequest.propertyName.invalid}"
        )
        String propertyName,

        @NotNull(message = "{NotNull.createApartmentRequest.address}")
        @Valid
        AddressDTO address,

        @NotNull(message = "{NotNull.createApartmentRequest.sleepingAreas}")
        @Valid
        SleepingAreasDTO sleepingAreas,

        @NotNull(message = "{NotNull.createApartmentRequest.guestCount}")
        @Min(value = 1, message = "{Min.createApartmentRequest.guestCount}")
        @Max(value = 50, message = "{Max.createApartmentRequest.guestCount}")
        Integer guestCount,

        @NotNull(message = "{NotNull.createApartmentRequest.bathroomCount}")
        @Min(value = 1, message = "{Min.createApartmentRequest.bathroomCount}")
        @Max(value = 50, message = "{Max.createApartmentRequest.bathroomCount}")
        Integer bathroomCount,

        @NotNull(message = "{NotNull.createApartmentRequest.allowChildren}")
        Boolean allowChildren,

        @NotNull(message = "{NotNull.createApartmentRequest.offerCots}")
        Boolean offerCots,

        @NotNull(message = "{NotNull.createApartmentRequest.aptSize}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{DecimalMin.createApartmentRequest.aptSize}")
        @Digits(integer = 10, fraction = 2, message = "{Digits.createApartmentRequest.aptSize}")
        BigDecimal aptSize,

        @NotNull(message = "{NotNull.createApartmentRequest.amenities}")
        List<@NotBlank(message = "{NotBlank.createApartmentRequest.amenities}") String> amenities,

        @NotNull(message = "{NotNull.createApartmentRequest.serveBreakfast}")
        Boolean serveBreakfast,

        @NotNull(message = "{NotNull.createApartmentRequest.isParkingAvailable}")
        ParkingPolicy isParkingAvailable,

        @NotNull(message = "{NotNull.createApartmentRequest.languages}")
        List<@NotBlank(message = "{NotBlank.createApartmentRequest.languages}") String> languages,

        @NotNull(message = "{NotNull.createApartmentRequest.smokingAllowed}")
        Boolean smokingAllowed,

        @NotNull(message = "{NotNull.createApartmentRequest.partiesAllowed}")
        Boolean partiesAllowed,

        @NotNull(message = "{NotNull.createApartmentRequest.petsAllowed}")
        PetsPolicy petsAllowed,

        @NotNull(message = "{NotNull.createApartmentRequest.checkInFrom}")
        LocalTime checkInFrom,

        @NotNull(message = "{NotNull.createApartmentRequest.checkInUntil}")
        LocalTime checkInUntil,

        @NotNull(message = "{NotNull.createApartmentRequest.checkOutFrom}")
        LocalTime checkOutFrom,

        @NotNull(message = "{NotNull.createApartmentRequest.checkOutUntil}")
        LocalTime checkOutUntil,

        @NotNull(message = "{NotNull.createApartmentRequest.photosCount}")
        @Min(value = 0, message = "{Min.createApartmentRequest.photosCount}")
        @Max(value = 200, message = "{Max.createApartmentRequest.photosCount}")
        Integer photosCount,

        @NotNull(message = "{NotNull.createApartmentRequest.pricePerNight}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{DecimalMin.createApartmentRequest.pricePerNight}")
        @Digits(integer = 10, fraction = 2, message = "{Digits.createApartmentRequest.pricePerNight}")
        BigDecimal pricePerNight
) {
    @Override
    @NonNull
    public String toString() {
        return "CreateApartmentRequest{" +
                "propertyName='" + propertyName + '\'' +
                ", address=" + address +
                ", sleepingAreas=" + sleepingAreas +
                ", guestCount=" + guestCount +
                ", bathroomCount=" + bathroomCount +
                ", allowChildren='" + allowChildren + '\'' +
                ", offerCots='" + offerCots + '\'' +
                ", aptSize='" + aptSize + '\'' +
                ", amenities=" + (amenities == null ? 0 : amenities.size()) +
                ", serveBreakfast='" + serveBreakfast + '\'' +
                ", isParkingAvailable='" + isParkingAvailable + '\'' +
                ", languages=" + (languages == null ? 0 : languages.size()) +
                ", smokingAllowed=" + smokingAllowed +
                ", partiesAllowed=" + partiesAllowed +
                ", petsAllowed='" + petsAllowed + '\'' +
                ", checkInFrom='" + checkInFrom + '\'' +
                ", checkInUntil='" + checkInUntil + '\'' +
                ", checkOutFrom='" + checkOutFrom + '\'' +
                ", checkOutUntil='" + checkOutUntil + '\'' +
                ", photosCount=" + photosCount +
                ", pricePerNight=" + pricePerNight +
                '}';
    }
}
