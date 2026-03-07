package gr.aueb.cf.property_renting_platform.DTOs.requests.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotNull Long roleId
) {}

