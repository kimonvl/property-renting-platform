package gr.aueb.cf.property_renting_platform.validators;

import gr.aueb.cf.property_renting_platform.DTOs.requests.guest.property.PropertySearchRequest;
import gr.aueb.cf.property_renting_platform.services.DictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchFiltersValidator implements Validator {

    private final DictionaryService dictionaryService;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return PropertySearchRequest.class == clazz;
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        PropertySearchRequest propertySearchRequest = (PropertySearchRequest) target;

        // minPrice < maxPrice
        validatePriceRange(errors, propertySearchRequest.minPrice(), propertySearchRequest.maxPrice());

        // check in before checkout & both in the future
        validateCheckInOut(errors, propertySearchRequest.checkIn(), propertySearchRequest.checkOut());

        // cross-check amenity codes with db
        validateAmenityCodes(errors, propertySearchRequest.amenities());
    }

    private void validateAmenityCodes(Errors errors, List<String> amenities) {
        if(!errors.hasFieldErrors("amenities")) {
            List<String> normalized = ValidatorUtils.getNormalisedStrings(amenities);
            if (normalized.isEmpty()) {
                return;
            }
            List<String> incorrectAmenityCodes = dictionaryService.findIncorrectAmenityCodes(normalized);
            if (!incorrectAmenityCodes.isEmpty()) {
                errors.rejectValue(
                        "amenities",
                        "search_filter_validator.amenities.incorrect_codes",
                        new Object[]{incorrectAmenityCodes},
                        null
                );
                String incorrectCodes = String.join(", ", incorrectAmenityCodes);
                log.warn("Search filter validation failed: Incorrect amenity codes given {}", incorrectCodes);
            }
        }
    }

    private static void validateCheckInOut(Errors errors, LocalDate checkIn, LocalDate checkOut) {
        if (!errors.hasFieldErrors("checkIn")
                && errors.hasFieldErrors("checkOut")) {
            if (checkIn.isBefore(LocalDate.now()) || checkOut.isBefore(LocalDate.now())) {
                if (checkIn.isBefore(LocalDate.now())){
                    log.warn("Search filter validation failed: CheckIn={} is in the past.", checkIn);
                }
                if (checkOut.isBefore(LocalDate.now())){
                    log.warn("Search filter validation failed: CheckOut={}", checkOut);
                }
                errors.rejectValue("checkIn", "search_filter_validator.check_in_out.in_past");
            }
            if (checkIn.isAfter(checkOut)) {
                log.warn("Search filter validation failed: CheckIn={} is after CheckOut={}", checkIn, checkOut);
                errors.rejectValue("checkOut", "search_filter_validator.check_in_out.range.invalid");
            }
        }
    }

    private void validatePriceRange(Errors errors, BigDecimal minPrice, BigDecimal maxPrice) {
        if (!errors.hasFieldErrors("minPrice")
                && !errors.hasFieldErrors("maxPrice")) {
            if (minPrice.compareTo(maxPrice) >= 1 ) {
                log.warn("Search filter validation failed: MinPrice={} is greater that MaxPrice={}", minPrice, maxPrice);
                errors.rejectValue("minPrice", "search_filter_validator.price.range.invalid");
            }
        }
    }
}
