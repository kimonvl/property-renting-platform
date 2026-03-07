package gr.aueb.cf.property_renting_platform.DTOs.responses.property;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AddressDTO(
        UUID propertyId,

        @NotBlank(message = "{NotBlank.addressDTO.country}")
        @Size(min = 2, max = 2, message = "{Size.addressDTO.country}")
        @Pattern(regexp = "^[A-Z]{2}$", message = "{Pattern.addressDTO.country}")
        String country, // ISO code like "GR"

        @NotBlank(message = "{NotBlank.addressDTO.city}")
        @Size(min = 3, max = 120, message = "{Size.addressDTO.city}")
        String city,

        @NotBlank(message = "{NotBlank.addressDTO.postCode}")
        @Pattern(regexp = "^\\d{5}$", message = "{Pattern.addressDTO.postCode}")
        String postCode,

        @NotBlank(message = "{NotBlank.addressDTO.street}")
        @Size(min = 2, max = 200, message = "{Size.addressDTO.street}")
        String street,

        // Optional but if present keep it clean
        @Size(max = 32, message = "{Size.addressDTO.streetNumber}")
        @Pattern(regexp = "^[0-9A-Za-z\\-\\/\\s]*$", message = "{Pattern.addressDTO.streetNumber}")
        String streetNumber,

        @Size(max = 10, message = "{Size.addressDTO.floorNumber}")
        @Pattern(regexp = "^[^\\p{Cntrl}]*$", message = "{Pattern.addressDTO.floorNumber}")
        String floorNumber
) {
}
