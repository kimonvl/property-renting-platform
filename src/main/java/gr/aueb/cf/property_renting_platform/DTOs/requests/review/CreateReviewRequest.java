package gr.aueb.cf.property_renting_platform.DTOs.requests.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateReviewRequest(
        @NotNull UUID bookingId,
        @Min(1) @Max(10) int rating,
        String positiveComment,
        String negativeComment
) {}
