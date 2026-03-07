package gr.aueb.cf.property_renting_platform.controllers;

import gr.aueb.cf.property_renting_platform.exceptions.InternalErrorException;
import gr.aueb.cf.property_renting_platform.services.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// TODO test stripe flow and webhook handling
@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final StripeWebhookService webhookService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<@NonNull String> webhook(
            HttpServletRequest request,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) throws InternalErrorException {
        String payload = null;
        try {
            payload = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            webhookService.handleEvent(event);
            return ResponseEntity.ok("ok");

        } catch (IOException e) {
            log.error("Request transformation failed for stripe webhook");
            throw new InternalErrorException("StripeWebhookRequest", "Failed to read request body");
        } catch (SignatureVerificationException e) {
            log.error("Stripe webhook event cannot be constructed due to invalid signature");
            throw new InternalErrorException("StripeWebhookEvent", "Invalid signature");
        }
    }

}
