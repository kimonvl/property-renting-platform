package gr.aueb.cf.property_renting_platform.controllers;

import gr.aueb.cf.property_renting_platform.DTOs.requests.booking.CreateBookingRequest;
import gr.aueb.cf.property_renting_platform.DTOs.responses.GenericResponse;
import gr.aueb.cf.property_renting_platform.DTOs.responses.booking.BookingStatusResponse;
import gr.aueb.cf.property_renting_platform.controllers.controller_utils.ResponseFactory;
import gr.aueb.cf.property_renting_platform.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;
import gr.aueb.cf.property_renting_platform.exceptions.ValidationException;
import gr.aueb.cf.property_renting_platform.models.booking.Booking;
import gr.aueb.cf.property_renting_platform.repos.BookingRepo;
import gr.aueb.cf.property_renting_platform.services.BookingService;
import gr.aueb.cf.property_renting_platform.validators.CreateBookingValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepo bookingRepo;
    private final CreateBookingValidator createBookingValidator;

    // TODO move find booking status to booking service
    @GetMapping("/{uuid}/status")
    public ResponseEntity<@NonNull GenericResponse<BookingStatusResponse>> getStatus(@PathVariable UUID uuid) {
        Booking b = bookingRepo.findByUuid(uuid).orElseThrow();
        return ResponseFactory.createResponse(
                new BookingStatusResponse(b.getUuid(), b.getStatus(), b.getPaymentStatus()),
                "Booking status fetched",
                HttpStatus.OK,
                true
        );
    }

    @PostMapping("/create")
    public ResponseEntity<@NonNull GenericResponse<UUID>> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            BindingResult bindingResult,
            Principal principal
    ) throws ValidationException, EntityInvalidArgumentException, EntityNotFoundException {
        createBookingValidator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationException("CreateBookingRequest", "Invalid booking data", bindingResult);
        }

        return new ResponseEntity<>(
                new GenericResponse<>(
                        bookingService.createBooking(request, principal.getName()),
                        "CreateBookingSucceeded",
                        "Booking created successfully",
                        true
                ),
                HttpStatus.CREATED
        );

    }

    @PostMapping("/delete/{bookingId}")
    public ResponseEntity<@NonNull GenericResponse<?>> deleteBooking(
            @PathVariable Long bookingId
    ) throws EntityNotFoundException {
        bookingService.deleteBooking(bookingId);
        return new ResponseEntity<>(
                new GenericResponse<>(
                        null,
                        "DeleteBookingSucceeded",
                        "Booking deleted successfully",
                        true
                ),
                HttpStatus.NO_CONTENT
        );

    }
}
