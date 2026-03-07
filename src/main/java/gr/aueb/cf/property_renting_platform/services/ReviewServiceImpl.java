package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.DTOs.responses.review.ReviewSummaryDTO;
import gr.aueb.cf.property_renting_platform.repos.ReviewRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService{

    private final ReviewRepo reviewRepo;

    @Override
    @Transactional(readOnly = true)
    public ReviewSummaryDTO getPropertyReviewSummary(Long propertyId) {
        return reviewRepo.getSummaryByPropertyId(propertyId);
    }
}
