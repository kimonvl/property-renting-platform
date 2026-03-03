package com.booking.booking_clone_backend.controllers.guest;

import com.booking.booking_clone_backend.DTOs.requests.guest.property.PropertySearchRequest;
import com.booking.booking_clone_backend.DTOs.responses.GenericResponse;
import com.booking.booking_clone_backend.DTOs.responses.property.PropertyDetailsDTO;
import com.booking.booking_clone_backend.DTOs.responses.property.PropertyShortDTO;
import com.booking.booking_clone_backend.constants.MessageConstants;
import com.booking.booking_clone_backend.controllers.controller_utils.ResponseFactory;
import com.booking.booking_clone_backend.exceptions.EntityNotFoundException;
import com.booking.booking_clone_backend.exceptions.ValidationException;
import com.booking.booking_clone_backend.services.GuestPropertyService;
import com.booking.booking_clone_backend.validators.SearchFiltersValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/guest/properties")
@RequiredArgsConstructor
public class GuestPropertiesController {

    private final GuestPropertyService guestPropertyService;
    private final SearchFiltersValidator searchFiltersValidator;

    @PostMapping("/search")
    public ResponseEntity<@NonNull GenericResponse<Page<@NonNull PropertyShortDTO>>> search(
            @Valid @RequestBody PropertySearchRequest request,
            BindingResult bindingResult
    ) throws ValidationException {
        searchFiltersValidator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationException("PropertySearchRequest", "Invalid search filters", bindingResult);
        }

        return new ResponseEntity<>(
                new GenericResponse<>(
                        guestPropertyService.search(request),
                        "PropertySearchSucceeded",
                        MessageConstants.PROPERTIES_FETCHED,
                        true
                        ),
                HttpStatus.OK);
    }

    @GetMapping("/details/{propertyId}")
    public ResponseEntity<@NonNull GenericResponse<@NonNull PropertyDetailsDTO>> getPropertyDetails(@PathVariable UUID propertyId) throws EntityNotFoundException {

        return new ResponseEntity<>(
                new GenericResponse<>(
                        guestPropertyService.getPropertyDetails(propertyId),
                        "PropertyDetailsFetched",
                        MessageConstants.PROPERTY_DETAILS_FETCHED,
                        true
                ),
                HttpStatus.OK
        );
    }
}
