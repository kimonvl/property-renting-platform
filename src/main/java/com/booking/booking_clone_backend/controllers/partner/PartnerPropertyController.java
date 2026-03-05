package com.booking.booking_clone_backend.controllers.partner;

import com.booking.booking_clone_backend.DTOs.requests.partner.apartment.CreatePropertyRequest;
import com.booking.booking_clone_backend.DTOs.responses.GenericResponse;
import com.booking.booking_clone_backend.constants.MessageConstants;
import com.booking.booking_clone_backend.exceptions.*;
import com.booking.booking_clone_backend.models.user.User;
import com.booking.booking_clone_backend.services.PartnerPropertyService;
import com.booking.booking_clone_backend.validators.CreateApartmentValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// TODO change endpoint to /partner/properties and add update endpoint
@RestController
@RequestMapping("/partner/properties")
@RequiredArgsConstructor
public class PartnerPropertyController {

    private final PartnerPropertyService apartmentService;
    private final CreateApartmentValidator createApartmentValidator;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<@NonNull GenericResponse<?>> createProperty(
            @Valid @RequestPart(value = "data") CreatePropertyRequest req,
            BindingResult bindingResult,
            @RequestPart(value = "photos") List<MultipartFile> photos,
            @RequestPart(value = "mainIndex") String mainIndex,
            @AuthenticationPrincipal User principal
            ) throws ValidationException, EntityInvalidArgumentException, InternalErrorException, FileUploadException, EntityNotFoundException {

        createApartmentValidator.validate(req, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationException("CreatePropertyRequest", "Invalid property data", bindingResult);
        }

        apartmentService.createProperty(req, photos, Integer.valueOf(mainIndex), principal);
        return ResponseEntity.ok(new GenericResponse<>(null, "CreatePropertySucceeded", MessageConstants.PROPERTY_CREATED, true));
    }
}
