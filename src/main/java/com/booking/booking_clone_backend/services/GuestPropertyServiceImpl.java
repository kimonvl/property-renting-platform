package com.booking.booking_clone_backend.services;

import com.booking.booking_clone_backend.DTOs.requests.guest.property.PropertySearchRequest;
import com.booking.booking_clone_backend.DTOs.responses.property.PropertyDetailsDTO;
import com.booking.booking_clone_backend.DTOs.responses.property.PropertyShortDTO;
import com.booking.booking_clone_backend.DTOs.responses.review.ReviewSummaryDTO;
import com.booking.booking_clone_backend.constants.MessageConstants;
import com.booking.booking_clone_backend.exceptions.EntityNotFoundException;
import com.booking.booking_clone_backend.mappers.PropertyCustomMapper;
import com.booking.booking_clone_backend.models.property.Property;
import com.booking.booking_clone_backend.repos.PropertyRepo;
import com.booking.booking_clone_backend.repos.ReviewRepo;
import com.booking.booking_clone_backend.repos.specifications.PropertySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestPropertyServiceImpl implements GuestPropertyService {

    private final ReviewService reviewService;
    private final PropertyRepo propertyRepo;
    private final ReviewRepo reviewRepo;
    private final PropertyCustomMapper propertyCustomMapper;



    @Override
    @Transactional(readOnly = true)
    public Page<@NonNull PropertyShortDTO> search(PropertySearchRequest request) {
        Specification<@NonNull Property> spec = Specification
                //.where(PropertySpecification.isPublished())
                .where(PropertySpecification.cityEqualsIgnoreCase(request.city()))
                .and(PropertySpecification.allowPets(request.pets()))
                .and(PropertySpecification.guestsAtLeast(request.maxGuest()))
                .and(PropertySpecification.bedroomsAtLeast(request.bedroomCount()))
                .and(PropertySpecification.bathroomsAtLeast(request.bathroomCount()))
                .and(PropertySpecification.priceBetween(request.minPrice(), request.maxPrice()))
                .and(PropertySpecification.availableBetween(request.checkIn(), request.checkOut()))
                .and(PropertySpecification.hasAllAmenities(request.amenities()));

        Page<@NonNull Property> page = propertyRepo.findAll(spec, PageRequest.of(request.page(), request.size()));

        List<@NonNull Property> properties = page.getContent();

        // If page is empty, avoid IN () query
        Map<Long, ReviewSummaryDTO> summaryMap;
        if (properties.isEmpty()) {
            summaryMap = Map.of();
        } else {
            List<Long> ids = properties.stream().map(Property::getId).toList();
            summaryMap = reviewRepo.getSummaryMap(ids);
        }

        List<PropertyShortDTO> dtoList = properties.stream()
                .map(p -> propertyCustomMapper.propertyToPropertyShortDTO(
                        p,
                        summaryMap.getOrDefault(p.getId(), new ReviewSummaryDTO(0, 0))
                ))
                .toList();
        log.info("Search completed with {} results", page.getTotalElements());
        return new PageImpl<>(
                dtoList,
                page.getPageable(),
                page.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyDetailsDTO getPropertyDetails(UUID propertyId) throws EntityNotFoundException {
        Property property = null;
        try {
            property = propertyRepo.findByUuid(propertyId)
                    .orElseThrow(() -> new EntityNotFoundException("GetDetailsProperty", "Property not found with id: " + propertyId));

            ReviewSummaryDTO reviewSummaryDTO = reviewService.getPropertyReviewSummary(property.getId());
            return propertyCustomMapper.propertyToPropertyDetailsDTO(property, reviewSummaryDTO);
        } catch (EntityNotFoundException e) {
            log.error("Failed to get property details for id={}", propertyId, e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPropertyExists(UUID propertyId) {
        return propertyRepo.existsByUuid(propertyId);
    }
}
