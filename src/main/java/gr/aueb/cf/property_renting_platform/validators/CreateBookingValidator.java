package gr.aueb.cf.property_renting_platform.validators;

import gr.aueb.cf.property_renting_platform.DTOs.requests.booking.CreateBookingRequest;
import gr.aueb.cf.property_renting_platform.services.GuestPropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateBookingValidator implements Validator {

    private final GuestPropertyService propertyService;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return CreateBookingRequest.class == clazz;
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
       CreateBookingRequest request = (CreateBookingRequest) target;

        validatePropertyExists(errors, request.propertyId());
        validateDateRange(errors, request.checkIn(), request.checkOut());
    }

    private static void validateDateRange(Errors errors, LocalDate checkIn, LocalDate checkOut) {
        if (!errors.hasFieldErrors("checkIn") && !errors.hasFieldErrors("checkOut")){
           if (checkIn.isAfter(checkOut)) {
               errors.rejectValue("checkIn", "create_booking.date_range.invalid");
           }
       }
    }

    private void validatePropertyExists(Errors errors, UUID propertyId) {
        if (!errors.hasFieldErrors("propertyId")) {
             if (!propertyService.isPropertyExists(propertyId)) {
                 errors.rejectValue("propertyId", "create_booking.property_id.not_found");
                 log.error("Create booking failed: Target property with id={}, doesn't exist", propertyId);
             }
        }
    }
}
