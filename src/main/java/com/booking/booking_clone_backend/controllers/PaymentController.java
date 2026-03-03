package com.booking.booking_clone_backend.controllers;

import com.booking.booking_clone_backend.DTOs.responses.GenericResponse;
import com.booking.booking_clone_backend.controllers.controller_utils.ResponseFactory;
import com.booking.booking_clone_backend.exceptions.EntityInvalidArgumentException;
import com.booking.booking_clone_backend.exceptions.EntityNotFoundException;
import com.booking.booking_clone_backend.exceptions.InternalErrorException;
import com.booking.booking_clone_backend.services.StripePaymentService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Locale;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final StripePaymentService stripePaymentService;
    private final MessageSource messageSource;

    @PostMapping("/create-intent")
    public ResponseEntity<@NonNull GenericResponse<String>> createIntent(
            @RequestBody UUID bookingId,
            Principal principal
    ) throws EntityInvalidArgumentException, EntityNotFoundException, InternalErrorException {
        return new ResponseEntity<>(
                new GenericResponse<>(
                        stripePaymentService.createPaymentIntent(bookingId, principal.getName()),
                        "CreateIntentSucceeded",
                        "Payment intent created successfully.",
                        true
                ),
                HttpStatus.CREATED
        );
    }
}
