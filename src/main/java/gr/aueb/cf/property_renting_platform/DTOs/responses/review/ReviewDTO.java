package gr.aueb.cf.property_renting_platform.DTOs.responses.review;

import gr.aueb.cf.property_renting_platform.DTOs.responses.user.UserDTO;

import java.time.Instant;
import java.util.UUID;

public record ReviewDTO(
        UUID id,
        int rating,
        String positiveComment,
        String negativeComment,
        String ownerResponse,
        Instant createdAt,
        Instant ownerRespondedAt,
        UserDTO guest
) {}
