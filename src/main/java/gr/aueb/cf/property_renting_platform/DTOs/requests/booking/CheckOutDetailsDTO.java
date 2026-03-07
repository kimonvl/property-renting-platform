package gr.aueb.cf.property_renting_platform.DTOs.requests.booking;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CheckOutDetailsDTO(

        Boolean travelingForWork,

        @NotBlank(message = "{NotBlank.checkOutDetailsDTO.title}")
        @Pattern(
                regexp = "^(Mr|Ms|Mrs)$",
                message = "{Pattern.checkOutDetailsDTO.title}"
        )
        String title,

        @NotBlank(message = "{NotBlank.checkOutDetailsDTO.firstName}")
        @Size(max = 60, message = "{Size.checkOutDetailsDTO.firstName}")
        String firstName,

        @NotBlank(message = "{NotBlank.checkOutDetailsDTO.lastName}")
        @Size(max = 60, message = "{Size.checkOutDetailsDTO.lastName}")
        String lastName,

        @NotBlank(message = "{NotBlank.checkOutDetailsDTO.email}")
        @Email(message = "{Email.checkOutDetailsDTO.email}")
        @Size(max = 254, message = "{Size.checkOutDetailsDTO.email}")
        String email,

        @NotBlank(message = "{NotBlank.checkOutDetailsDTO.phoneCountryCode}")
        @Pattern(
                regexp = "^\\+?[1-9]\\d{0,2}$",
                message = "{Pattern.checkOutDetailsDTO.phoneCountryCode}"
        )
        String phoneCountryCode,

        @NotBlank(message = "{NotBlank.checkOutDetailsDTO.phoneNumber}")
        @Pattern(
                regexp = "^[0-9()\\-\\s]{5,20}$",
                message = "{Pattern.checkOutDetailsDTO.phoneNumber}"
        )
        String phoneNumber,

        @Size(max = 1000, message = "{Size.checkOutDetailsDTO.specialRequest}")
        String specialRequest
) {}