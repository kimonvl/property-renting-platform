package com.booking.booking_clone_backend.services;

import com.booking.booking_clone_backend.DTOs.domain.BedSummaryResult;
import com.booking.booking_clone_backend.DTOs.requests.partner.apartment.BedType;
import com.booking.booking_clone_backend.DTOs.requests.partner.apartment.BedroomDTO;
import com.booking.booking_clone_backend.DTOs.requests.partner.apartment.CreatePropertyRequest;
import com.booking.booking_clone_backend.DTOs.requests.partner.apartment.SleepingAreasDTO;
import com.booking.booking_clone_backend.DTOs.responses.property.AddressDTO;
import com.booking.booking_clone_backend.exceptions.EntityInvalidArgumentException;
import com.booking.booking_clone_backend.exceptions.EntityNotFoundException;
import com.booking.booking_clone_backend.exceptions.FileUploadException;
import com.booking.booking_clone_backend.exceptions.InternalErrorException;
import com.booking.booking_clone_backend.mappers.PropertyCustomMapper;
import com.booking.booking_clone_backend.models.static_data.Amenity;
import com.booking.booking_clone_backend.models.static_data.Language;
import com.booking.booking_clone_backend.models.property.PropertyLanguage;
import com.booking.booking_clone_backend.models.property.*;
import com.booking.booking_clone_backend.models.static_data.Country;
import com.booking.booking_clone_backend.models.user.User;
import com.booking.booking_clone_backend.repos.AmenitiesRepo;
import com.booking.booking_clone_backend.repos.CountryRepo;
import com.booking.booking_clone_backend.repos.LanguageRepo;
import com.booking.booking_clone_backend.repos.PropertyRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class PartnerPropertyServiceImpl implements PartnerPropertyService {

    private final CloudinaryService cloudinaryService;
    private final DictionaryService dictionaryService;
    private final PropertyRepo propertyRepo;
    private final AmenitiesRepo amenitiesRepo;
    private final CountryRepo countryRepo;
    private final LanguageRepo languageRepo;
    private final PropertyCustomMapper propertyCustomMapper;

    @Override
    @PreAuthorize("hasAnyAuthority('CREATE_PROPERTY')")
    @Transactional(rollbackFor = {FileUploadException.class, EntityInvalidArgumentException.class, InternalErrorException.class})
    public void createProperty(CreatePropertyRequest request, List<MultipartFile> photos, Integer mainIndex, User user)
            throws FileUploadException, EntityInvalidArgumentException, InternalErrorException, EntityNotFoundException {
        try {
            BedSummaryResult result = getBedSummaryResult(request.sleepingAreas());
            Property savedProperty = propertyRepo.save(propertyCustomMapper.createApartmentRequestToProperty(request, result, user));

            //Property-Amenity
            addAmenities(request.amenities(), savedProperty);

            //Property-Address
            addAddress(request.address(), savedProperty);

            //Property-Languages
            addLanguages(request.languages(), savedProperty);

            //Upload photos to cloudinary
            addPhotos(photos, mainIndex, savedProperty);

            log.info("Property created successfully with id={}", savedProperty.getId());
            propertyRepo.save(savedProperty);
        } catch (EntityInvalidArgumentException | FileUploadException | EntityNotFoundException e) {
            log.warn("Property creation failed for user with email ={}. Message={}", user.getEmail(), e.getMessage());
            throw e;
        } catch (JsonProcessingException e) {
            log.error("Property creation failed during json processing.");
            throw new InternalErrorException("CreateApartmentJsonProcessing", "Failed to create apartment due to json processing error.");
        }
    }

    private void addPhotos(List<MultipartFile> photos, Integer mainIndex, Property savedProperty) throws FileUploadException {
        String folder = "booking/properties/" + savedProperty.getId();
        for (int i = 0; i < photos.size(); i++) {
            CloudinaryService.UploadResult res = cloudinaryService.uploadImage(photos.get(i), folder, "photo_" + i);
            PropertyPhoto pp = new PropertyPhoto();
            pp.setUrl(res.url());
            pp.setPublicId(res.publicId());

            savedProperty.addPropertyPhoto(pp);
            if (mainIndex == i) {
                savedProperty.setMainPhotoUrl(res.url());
            }
        }

    }

    private void addLanguages(List<String> languageCodes, Property savedProperty) throws EntityInvalidArgumentException {
        List<Language> languages = languageRepo.findByCodeInIgnoreCase(languageCodes);
        if (languages.isEmpty())
            throw new EntityInvalidArgumentException("CreateApartmentLanguage", "Failed to create apartment. No valid languages provided.");
        for (Language lang : languages) {
            PropertyLanguage pl = new PropertyLanguage();
            pl.setLanguage(lang);
            pl.setId(new PropertyLanguage.PropertyLanguageId(savedProperty.getId(), lang.getId()));

            savedProperty.addPropertyLanguage(pl);
        }
    }


    private void addAmenities(List<String> amenityCodes, Property savedProperty) throws EntityInvalidArgumentException, EntityNotFoundException {
        if (!dictionaryService.findIncorrectAmenityCodes(amenityCodes).isEmpty()) {
            throw new EntityInvalidArgumentException("CreateApartmentAmenity", "Failed to create apartment. Invalid amenity codes provided.");
        }
        List<Amenity> amenities = amenitiesRepo.findByCodeIn(amenityCodes);
        if (amenities.isEmpty())
            throw new EntityNotFoundException("CreateApartmentAmenity", "Failed to create apartment. No valid amenities provided.");
        for (Amenity amenity : amenities) {
            savedProperty.addAmenity(amenity);
        }
    }

    private void addAddress(AddressDTO address, Property savedProperty) throws EntityInvalidArgumentException {
        Country country = countryRepo.findByCode(address.country())
                .orElseThrow(() -> new EntityInvalidArgumentException("CreateApartmentCountry", "Failed to create apartment. Invalid country code provided: " + address.country()));

        PropertyAddress pa = getPropertyAddress(address, country, savedProperty);
        savedProperty.setAddress(pa);
    }

    private static PropertyAddress getPropertyAddress(AddressDTO address, Country country, Property savedProperty) {
        PropertyAddress pa = new PropertyAddress();
        pa.setProperty(savedProperty);
        pa.setCountry(country);
        pa.setCity(address.city());
        pa.setPostcode(address.postCode());
        pa.setStreet(address.street());
        pa.setStreetNumber(address.streetNumber());
        return pa;
    }

    public static BedSummaryResult getBedSummaryResult(SleepingAreasDTO sleepingAreas) {
        Integer bedCount = 0;
        int livingRoomCount = 0;
        Map<BedType, Integer> totalBeds = new HashMap<>();
        for (BedroomDTO bedroom : sleepingAreas.bedrooms()) {
            if (bedroom != null) {
                for (Map.Entry<BedType, Integer> entry : bedroom.beds().entrySet()) {
                    bedCount += entry.getValue();
                    totalBeds.compute(
                            entry.getKey(),
                            (key, value) -> value == null ? entry.getValue() : value + entry.getValue()
                    );
                }
            }
        }
        if (sleepingAreas.livingRoom() != null) {
            livingRoomCount += 1;
            for (Map.Entry<BedType, Integer> entry : sleepingAreas.livingRoom().beds().entrySet()) {
                bedCount += entry.getValue();
                totalBeds.compute(
                        entry.getKey(),
                        (key, value) -> value == null ? entry.getValue() : value + entry.getValue()
                );
            }
        }
        StringBuilder bedSummary = new StringBuilder(bedCount + " beds (");
        for (Map.Entry<BedType, Integer> entry : totalBeds.entrySet()) {
            if (entry.getValue() > 0)
                bedSummary.append(entry.getValue()).append(" ").append(entry.getKey().name()).append(", ");
        }
        bedSummary.delete(bedSummary.length()-2, bedSummary.length());
        bedSummary.append(")");
        return new BedSummaryResult(bedCount, livingRoomCount, bedSummary);
    }
}
