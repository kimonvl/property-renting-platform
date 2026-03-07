package gr.aueb.cf.property_renting_platform.controllers.guest;

import gr.aueb.cf.property_renting_platform.DTOs.requests.guest.property.PropertySearchRequest;
import gr.aueb.cf.property_renting_platform.DTOs.responses.GenericResponse;
import gr.aueb.cf.property_renting_platform.DTOs.responses.property.PropertyDetailsDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.property.PropertyShortDTO;
import gr.aueb.cf.property_renting_platform.constants.MessageConstants;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;
import gr.aueb.cf.property_renting_platform.exceptions.ValidationException;
import gr.aueb.cf.property_renting_platform.services.GuestPropertyService;
import gr.aueb.cf.property_renting_platform.validators.SearchFiltersValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
