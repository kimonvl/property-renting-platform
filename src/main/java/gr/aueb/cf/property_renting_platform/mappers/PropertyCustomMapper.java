package gr.aueb.cf.property_renting_platform.mappers;

import gr.aueb.cf.property_renting_platform.DTOs.domain.BedSummaryResult;
import gr.aueb.cf.property_renting_platform.DTOs.requests.partner.apartment.CreatePropertyRequest;
import gr.aueb.cf.property_renting_platform.DTOs.responses.property.PropertyDetailsDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.property.PropertyShortDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.review.ReviewSummaryDTO;
import gr.aueb.cf.property_renting_platform.models.attachment.PropertyAttachment;
import gr.aueb.cf.property_renting_platform.models.property.CurrencyCode;
import gr.aueb.cf.property_renting_platform.models.property.Property;
import gr.aueb.cf.property_renting_platform.models.property.PropertyStatus;
import gr.aueb.cf.property_renting_platform.models.property.PropertyType;
import gr.aueb.cf.property_renting_platform.models.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
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
                dictionaryMapper.amenitiesToDtoSet(property.getAllAmenities()),
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
                dictionaryMapper.amenitiesToDtoSet(property.getAllAmenities()),
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
                propertyPhotosToString(property.getAllAttachments()),
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

    private Set<String> propertyPhotosToString(Set<PropertyAttachment> attachments) {
        Set<String> photoUrls = new HashSet<>();
        System.out.println("Attachments size: " + attachments.size());
        for (PropertyAttachment photo : attachments) {
            photoUrls.add(photo.getUrl());
        }
        return photoUrls;
    }

}
