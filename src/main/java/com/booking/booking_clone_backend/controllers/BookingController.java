package com.booking.booking_clone_backend.controllers;

import com.booking.booking_clone_backend.DTOs.requests.booking.CreateBookingRequest;
import com.booking.booking_clone_backend.DTOs.responses.GenericResponse;
import com.booking.booking_clone_backend.DTOs.responses.booking.BookingStatusResponse;
import com.booking.booking_clone_backend.constants.MessageConstants;
import com.booking.booking_clone_backend.controllers.controller_utils.ResponseFactory;
import com.booking.booking_clone_backend.exceptions.EntityInvalidArgumentException;
import com.booking.booking_clone_backend.exceptions.EntityNotFoundException;
import com.booking.booking_clone_backend.exceptions.ValidationException;
import com.booking.booking_clone_backend.models.booking.Booking;
import com.booking.booking_clone_backend.repos.BookingRepo;
import com.booking.booking_clone_backend.services.BookingService;
import com.booking.booking_clone_backend.validators.CreateBookingValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Locale;
import java.util.Map;
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
