package gr.aueb.cf.property_renting_platform.controllers;

import gr.aueb.cf.property_renting_platform.DTOs.requests.auth.LoginRequest;
import gr.aueb.cf.property_renting_platform.DTOs.requests.auth.RegisterRequest;
import gr.aueb.cf.property_renting_platform.DTOs.responses.GenericResponse;
import gr.aueb.cf.property_renting_platform.DTOs.responses.auth.AuthResponse;
import gr.aueb.cf.property_renting_platform.DTOs.responses.user.UserDTO;
import gr.aueb.cf.property_renting_platform.constants.MessageConstants;
import gr.aueb.cf.property_renting_platform.exceptions.*;
import gr.aueb.cf.property_renting_platform.services.AuthServiceImpl;
import gr.aueb.cf.property_renting_platform.validators.RegisterValidator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Locale;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String REFRESH_COOKIE = "refresh_token";

    private final AuthServiceImpl authService;
    private final RegisterValidator registerValidator;
    private final MessageSource messageSource;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site}")
    private String sameSite;

    @Value("${app.jwt.refresh-days}")
    private long refreshDays;

    @PostMapping("/register")
    public ResponseEntity<@NonNull GenericResponse<UserDTO>> register(
            @Valid @RequestBody RegisterRequest req,
            BindingResult bindingResult,
            Locale locale
    ) throws EntityInvalidArgumentException, EntityAlreadyExistsException, ValidationException {
        registerValidator.validate(req, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationException("RegisterRequest", "Validation failed for registration request", bindingResult);
        }
        return new ResponseEntity<>(
                new GenericResponse<>(
                        authService.register(req),
                        "RegisterSucceeded",
                        messageSource.getMessage("auth.register.succeeded", null, MessageConstants.REGISTERED, locale),
                        true
                ),
                HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<@NonNull GenericResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest req,
            BindingResult bindingResult,
            HttpServletResponse response,
            Locale locale
    ) throws EntityInvalidArgumentException, InternalErrorException, EntityNotFoundException, ValidationException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException("LoginRequest", "Validation failed for login request", bindingResult);
        }
        var result = authService.login(req);
        setRefreshCookie(response, result.refreshToken());
        return new ResponseEntity<>(
                new GenericResponse<>(
                        new AuthResponse(result.accessToken(), result.userDTO()),
                        "LoginSucceeded",
                        messageSource.getMessage("auth.login.succeeded", null, MessageConstants.LOGGED_IN, locale),
                        true
                ),
                HttpStatus.OK);
    }

    /**
     * Frontend calls this with axios { withCredentials: true }
     * We read refresh token from HttpOnly cookie.
     */
    @PostMapping("/refresh")
    public ResponseEntity<@NonNull GenericResponse<AuthResponse>> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response,
            Locale locale
    ) throws EntityInvalidArgumentException, EntityNotFoundException {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new EntityInvalidArgumentException("EmptyRefreshToken", "Refresh token is missing from cookie");
        }

        try {
            var result = authService.refresh(refreshToken);
            setRefreshCookie(response, result.refreshToken());
            return new ResponseEntity<>(
                    new GenericResponse<>(
                            new AuthResponse(result.accessToken(), result.userDTO()),
                            "TokenRefreshSucceeded",
                            messageSource.getMessage("auth.refresh.succeeded", null, MessageConstants.TOKEN_REFRESHED, locale),
                            true
                    ),
                    HttpStatus.ACCEPTED);
        } catch (EntityInvalidArgumentException | EntityNotFoundException e) {
            clearRefreshCookie(response);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<@NonNull GenericResponse<?>> logout(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletResponse response,
            Locale locale
    ) throws EntityNotFoundException {
        try {
            if (refreshToken != null && !refreshToken.isBlank()) {
                authService.logout(refreshToken);
            }
            clearRefreshCookie(response);
            return new ResponseEntity<>(
                    new GenericResponse<>(
                            null,
                            "LogoutSucceeded",
                            messageSource.getMessage("auth.logout.succeeded", null, MessageConstants.LOGGED_OUT, locale),
                            true
                    ),
                    HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            clearRefreshCookie(response);
            throw e;
        }
    }

    private void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(cookieSecure) // false on localhost, true in prod
                .path("/auth")        // only sent to /auth/*
                .sameSite(sameSite)   // Lax recommended for same-site SPA dev
                .maxAge(Duration.ofDays(refreshDays))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cleared = ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/auth")
                .sameSite(sameSite)
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cleared.toString());
    }
}
