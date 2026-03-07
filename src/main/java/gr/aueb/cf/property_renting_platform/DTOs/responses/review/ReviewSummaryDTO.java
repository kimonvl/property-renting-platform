package gr.aueb.cf.property_renting_platform.DTOs.responses.review;

public record ReviewSummaryDTO(
        double averageRating,
        long reviewCount
) {}
