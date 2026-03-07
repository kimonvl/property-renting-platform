package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.DTOs.responses.review.ReviewSummaryDTO;

public interface ReviewService {
    ReviewSummaryDTO getPropertyReviewSummary(Long propertyId);
}
