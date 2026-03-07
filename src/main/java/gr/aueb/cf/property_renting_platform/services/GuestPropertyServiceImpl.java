package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.DTOs.requests.guest.property.PropertySearchRequest;
import gr.aueb.cf.property_renting_platform.DTOs.responses.property.PropertyDetailsDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.property.PropertyShortDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.review.ReviewSummaryDTO;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;
import gr.aueb.cf.property_renting_platform.mappers.PropertyCustomMapper;
import gr.aueb.cf.property_renting_platform.models.property.Property;
import gr.aueb.cf.property_renting_platform.repos.PropertyRepo;
import gr.aueb.cf.property_renting_platform.repos.ReviewRepo;
import gr.aueb.cf.property_renting_platform.repos.specifications.PropertySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

        List<@NonNull Long> ids = page.getContent().stream().map(Property::getId).toList();

        // If page is empty, avoid IN () query
        Map<Long, ReviewSummaryDTO> summaryMap;
        List<Property> properties = new ArrayList<>();
        if (ids.isEmpty()) {
            summaryMap = Map.of();
        } else {
            properties = propertyRepo.findAllByIdInWithAddressAndAmenities(ids);
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
