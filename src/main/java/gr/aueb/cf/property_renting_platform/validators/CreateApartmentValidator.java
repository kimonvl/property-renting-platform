package gr.aueb.cf.property_renting_platform.validators;

import gr.aueb.cf.property_renting_platform.DTOs.requests.partner.apartment.CreatePropertyRequest;
import gr.aueb.cf.property_renting_platform.services.DictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateApartmentValidator implements Validator {

    private final DictionaryService dictionaryService;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return CreatePropertyRequest.class == clazz;
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        CreatePropertyRequest createPropertyRequest = (CreatePropertyRequest) target;

        // check sleeping areas to have at least 1 bed and total capacity based on bed types be equal or greater than max guest count
        validateSleepingAreasAndGuestCount(errors, createPropertyRequest);
        // check if there is at least 1 amenity check if amenities strings are legit amenity codes in db
        validateAmenities(errors, createPropertyRequest.amenities());
        // check if there is at least 1 language and check language codes with db
        validateLanguages(errors, createPropertyRequest.languages());
        //check in from is after check out until
        validateCheckInOutTimes(
                errors,
                createPropertyRequest.checkInFrom(),
                createPropertyRequest.checkInUntil(),
                createPropertyRequest.checkOutFrom(),
                createPropertyRequest.checkOutUntil()
                );

    }

    private void validateCheckInOutTimes(
            Errors errors,
            LocalTime checkInFrom,
            LocalTime checkInUntil,
            LocalTime checkOutFrom,
            LocalTime checkOutUntil
    ) {
        if (errors.hasFieldErrors("checkInFrom") ||
                errors.hasFieldErrors("checkInUntil") ||
                errors.hasFieldErrors("checkOutFrom") ||
                errors.hasFieldErrors("checkOutUntil") ||
                checkInFrom == null || checkInUntil == null || checkOutFrom == null || checkOutUntil == null) {
            return;
        }

        if (checkInFrom.isAfter(checkInUntil)) {
            errors.rejectValue("checkInFrom", "checkInFrom.invalid_range");
            log.warn("Apartment creation failed. CheckInFrom={} is after CheckInUntil={}", checkInFrom, checkInUntil);
        }

        if (checkOutFrom.isAfter(checkOutUntil)) {
            errors.rejectValue("checkOutFrom", "checkOutFrom.invalid_range");
            log.warn("Apartment creation failed. CheckOutFrom={} is after CheckOutUntil={}", checkOutFrom, checkOutUntil);
        }

        if (checkOutUntil.isAfter(checkInFrom)) {
            errors.rejectValue("checkOutUntil", "checkOutUntil.invalid_range");
            log.warn("Apartment creation failed. CheckOutUntil={} is after CheckInFrom={}", checkOutUntil, checkInFrom);
        }
    }

    private void validateLanguages(Errors errors, List<String> codes) {
        if (!errors.hasFieldErrors("languages")) {
            List<String> normalized = ValidatorUtils.getNormalisedStrings(codes);
            if (normalized.isEmpty()) {
                errors.rejectValue("languages", "languages.empty");
                log.warn("Apartment creation failed. 0 languages provided");
                return;
            }
            List<String> incorrectLanguageCodes = dictionaryService.findIncorrectLanguageCodes(normalized);
            if (!incorrectLanguageCodes.isEmpty()) {
                errors.rejectValue(
                        "languages",
                        "languages.incorrect_codes",
                        new Object[]{incorrectLanguageCodes},
                        null
                );
                String incorrectCodes = String.join(", ", incorrectLanguageCodes);
                log.warn("Apartment creation failed. Incorrect language codes given: {}", incorrectCodes);
            }
        }
    }

    private void validateAmenities(Errors errors, List<String> amenities) {
        if (!errors.hasFieldErrors("amenities")) {
            List<String> normalized = ValidatorUtils.getNormalisedStrings(amenities);
            if (normalized.isEmpty()) {
                errors.rejectValue("amenities", "amenities.empty");
                log.warn("Apartment creation failed. 0 amenities provided");
                return;
            }
            List<String> incorrectAmenityCodes = dictionaryService.findIncorrectAmenityCodes(normalized);
            if (!incorrectAmenityCodes.isEmpty()) {
                errors.rejectValue(
                        "amenities",
                        "amenities.incorrect_codes",
                        new Object[]{incorrectAmenityCodes},
                        null
                );
                String incorrectCodes = String.join(", ", incorrectAmenityCodes);
               log.warn("Apartment creation failed. Incorrect amenity codes given: {}", incorrectCodes);
            }
        }
    }

    private static void validateSleepingAreasAndGuestCount(Errors errors, CreatePropertyRequest createPropertyRequest) {
        if (!errors.hasFieldErrors("sleepingAreas")) {
            AtomicInteger allowedGuestCount = new AtomicInteger();
            int plusCot = 0;
            createPropertyRequest.sleepingAreas().bedrooms().forEach(bedroomDTO -> {
                bedroomDTO.beds()
                        .forEach((bedType, amount) -> allowedGuestCount.set(allowedGuestCount.intValue() + bedType.getCapacity() * amount) );
            });
            createPropertyRequest.sleepingAreas().livingRoom().beds()
                    .forEach((bedType, amount) -> allowedGuestCount.set(allowedGuestCount.intValue() + bedType.getCapacity() * amount));

            if (createPropertyRequest.allowChildren() && createPropertyRequest.offerCots()) {
                allowedGuestCount.set(allowedGuestCount.intValue() + 1);
                plusCot++;
            }
            if (createPropertyRequest.guestCount() > allowedGuestCount.intValue()) {
                errors.rejectValue("guestCount", "sleeping_areas.mismatch");
                log.warn("Apartment creation failed. Max guest count={} exceeds sleeping areas beds' total capacity={} ", createPropertyRequest.guestCount(), allowedGuestCount.intValue());
            } else if (allowedGuestCount.intValue() - plusCot <= 0) {
                errors.rejectValue("sleepingAreas", "sleeping_areas.zero_beds");
                log.warn("Apartment creation failed. 0 beds provided");
            }
        }
    }


}
