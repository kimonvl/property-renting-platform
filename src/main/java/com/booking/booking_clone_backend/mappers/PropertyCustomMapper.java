package com.booking.booking_clone_backend.mappers;

import com.booking.booking_clone_backend.DTOs.domain.BedSummaryResult;
import com.booking.booking_clone_backend.DTOs.requests.partner.apartment.CreatePropertyRequest;
import com.booking.booking_clone_backend.DTOs.responses.dictionaries.amenity.AmenityDTO;
import com.booking.booking_clone_backend.DTOs.responses.property.PropertyDetailsDTO;
import com.booking.booking_clone_backend.DTOs.responses.property.PropertyShortDTO;
import com.booking.booking_clone_backend.DTOs.responses.review.ReviewSummaryDTO;
import com.booking.booking_clone_backend.models.property.*;
import com.booking.booking_clone_backend.models.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PropertyCustomMapper {

    private final DictionaryMapper dictionaryMapper;
    private final AddressMapper addressMapper;
    private final ObjectMapper objectMapper;

    public Property createApartmentRequestToProperty(
            CreatePropertyRequest request,
            BedSummaryResult bedSummaryResult,
            User user
    ) throws JsonProcessingException {
        Property property = new Property();
        property.setOwner(user);
        property.setType(PropertyType.APARTMENT);
        property.setStatus(PropertyStatus.DRAFT);
        property.setName(request.propertyName());
        property.setPricePerNight(request.pricePerNight());
        property.setCurrency(CurrencyCode.EUR);
        property.setMaxGuests(request.guestCount());
        property.setSizeSqm(request.aptSize());
        property.setChildrenAllowed(request.allowChildren());
        property.setCotsOffered(request.offerCots());
        property.setBreakfastServed(request.serveBreakfast());
        property.setParkingPolicy(request.isParkingAvailable());
        property.setSmokingAllowed(request.smokingAllowed());
        property.setPartiesAllowed(request.partiesAllowed());
        property.setPetsPolicy(request.petsAllowed());
        property.setCheckInFrom(request.checkInFrom());
        property.setCheckInUntil(request.checkInUntil());
        property.setCheckOutFrom(request.checkOutFrom());
        property.setCheckOutUntil(request.checkOutUntil());
        property.setBathrooms(request.bathroomCount());

        property.setSleepingAreasJson(objectMapper.writeValueAsString(request.sleepingAreas()));

        property.setLivingRoomCount(bedSummaryResult.livingRoomCount());
        property.setBedroomCount(request.sleepingAreas().bedrooms().size());
        property.setBedCount(bedSummaryResult.bedCount());
        property.setBedSummary(bedSummaryResult.bedSummary().toString());
        return property;
    }

    public PropertyShortDTO propertyToPropertyShortDTO(Property property, ReviewSummaryDTO reviewSummaryDTO) {

        return new PropertyShortDTO(
                property.getUuid(),
                propertyAmenitiesToAmenityDTO(property.getAllPropertyAmenities()),
                addressMapper.toDto(property.getAddress()),
                property.getType(),
                property.getStatus(),
                property.getName(),
                property.getPricePerNight(),
                property.getCurrency(),
                property.getSizeSqm(),
                property.getMaxGuests(),
                property.getBathrooms(),
                property.getLivingRoomCount(),
                property.getBedroomCount(),
                property.getBedSummary(),
                property.getMainPhotoUrl(),
                reviewSummaryDTO
        );
    }

    public PropertyDetailsDTO propertyToPropertyDetailsDTO(Property property, ReviewSummaryDTO reviewSummaryDTO) {
        DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
        return new PropertyDetailsDTO(
                property.getUuid(),
                propertyAmenitiesToAmenityDTO(property.getAllPropertyAmenities()),
                addressMapper.toDto(property.getAddress()),
                property.getType(),
                property.getName(),
                property.getPricePerNight(),
                property.getCurrency(),
                property.getSizeSqm(),
                property.getMaxGuests(),
                property.getBathrooms(),
                property.getLivingRoomCount(),
                property.getBedroomCount(),
                property.getBedSummary(),
                propertyPhotosToString(property.getAllPropertyPhotos()),
                property.getMainPhotoUrl(),
                reviewSummaryDTO,

                property.getCheckInFrom().format(TIME_FMT),
                property.getCheckInUntil().format(TIME_FMT),
                property.getCheckOutFrom().format(TIME_FMT),
                property.getCheckOutUntil().format(TIME_FMT),
                property.getChildrenAllowed(),
                property.getCotsOffered(),
                property.getSmokingAllowed(),
                property.getPartiesAllowed(),
                property.getPetsPolicy()
        );
    }

    private Set<String> propertyPhotosToString(List<PropertyPhoto> photos) {
        Set<String> photoUrls = new HashSet<>();
        for (PropertyPhoto pp : photos) {
            photoUrls.add(pp.getUrl());
        }
        return photoUrls;
    }

    private Set<AmenityDTO> propertyAmenitiesToAmenityDTO(Set<PropertyAmenity> propertyAmenities) {
        Set<AmenityDTO> amenityDTOs = new HashSet<>();
        for (PropertyAmenity pa : propertyAmenities) {
            amenityDTOs.add(dictionaryMapper.amenityToDto(pa.getAmenity()));
        }
        return amenityDTOs;
    }
}
