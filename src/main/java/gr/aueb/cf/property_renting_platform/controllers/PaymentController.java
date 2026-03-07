package gr.aueb.cf.property_renting_platform.controllers;

import gr.aueb.cf.property_renting_platform.DTOs.responses.GenericResponse;
import gr.aueb.cf.property_renting_platform.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;
import gr.aueb.cf.property_renting_platform.exceptions.InternalErrorException;
import gr.aueb.cf.property_renting_platform.services.StripePaymentService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
