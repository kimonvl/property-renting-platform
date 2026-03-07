package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.DTOs.domain.BedSummaryResult;
import gr.aueb.cf.property_renting_platform.DTOs.requests.partner.apartment.BedType;
import gr.aueb.cf.property_renting_platform.DTOs.requests.partner.apartment.BedroomDTO;
import gr.aueb.cf.property_renting_platform.DTOs.requests.partner.apartment.CreatePropertyRequest;
import gr.aueb.cf.property_renting_platform.DTOs.requests.partner.apartment.SleepingAreasDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.property.AddressDTO;
import gr.aueb.cf.property_renting_platform.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;
import gr.aueb.cf.property_renting_platform.exceptions.FileUploadException;
import gr.aueb.cf.property_renting_platform.exceptions.InternalErrorException;
import gr.aueb.cf.property_renting_platform.mappers.AddressMapper;
import gr.aueb.cf.property_renting_platform.mappers.PropertyCustomMapper;
import gr.aueb.cf.property_renting_platform.models.Address;
import gr.aueb.cf.property_renting_platform.models.attachment.PropertyAttachment;
import gr.aueb.cf.property_renting_platform.models.property.Property;
import gr.aueb.cf.property_renting_platform.models.static_data.Amenity;
import gr.aueb.cf.property_renting_platform.models.static_data.Language;
import gr.aueb.cf.property_renting_platform.models.static_data.Country;
import gr.aueb.cf.property_renting_platform.models.user.User;
import gr.aueb.cf.property_renting_platform.repos.AmenitiesRepo;
import gr.aueb.cf.property_renting_platform.repos.CountryRepo;
import gr.aueb.cf.property_renting_platform.repos.LanguageRepo;
import gr.aueb.cf.property_renting_platform.repos.PropertyRepo;
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
    private final AddressMapper addressMapper;

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
            PropertyAttachment pa = new PropertyAttachment();
            pa.setUrl(res.url());
            pa.setPublicId(res.publicId());
            savedProperty.addAttachment(pa);
            if (mainIndex == i) {
                // TODO store the id as well
                savedProperty.setMainPhotoUrl(res.url());
            }
        }

    }

    private void addLanguages(List<String> languageCodes, Property savedProperty) throws EntityInvalidArgumentException, EntityNotFoundException {
        if (!dictionaryService.findIncorrectLanguageCodes(languageCodes).isEmpty()) {
            throw new EntityInvalidArgumentException("CreateApartmentLanguage", "Failed to create apartment. Invalid language codes provided.");
        }
        List<Language> languages = languageRepo.findByCodeInIgnoreCase(languageCodes);
        if (languages.isEmpty())
            throw new EntityNotFoundException("CreateApartmentLanguage", "Failed to create apartment. No valid amenities provided.");
        for (Language language : languages) {
            savedProperty.addLanguage(language);
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

        Address pa = getPropertyAddress(address, country);
        savedProperty.setAddress(pa);
    }

    private Address getPropertyAddress(AddressDTO address, Country country) {
        return addressMapper.toAddressEntity(address, country);
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
