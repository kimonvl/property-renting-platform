package gr.aueb.cf.property_renting_platform.validators;

import gr.aueb.cf.property_renting_platform.DTOs.requests.auth.RegisterRequest;
import gr.aueb.cf.property_renting_platform.services.AuthServiceImpl;
import gr.aueb.cf.property_renting_platform.services.DictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterValidator implements Validator {

    private final AuthServiceImpl authService;
    private final DictionaryService dictionaryService;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return RegisterRequest.class == clazz;
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        RegisterRequest registerRequest = (RegisterRequest) target;
        if (!errors.hasFieldErrors("email")) {
            if (authService.isUserExists(registerRequest.email())) {
                errors.rejectValue("email", "username.user.exists");
                log.warn("Registration failed. User with email={} already exists", registerRequest.email());
            }
        }
        if (!errors.hasFieldErrors("country")) {
            if (!dictionaryService.isCountryExists(registerRequest.country())) {
                errors.rejectValue("country", "country.invalid");
                log.warn("Registration failed. Country with code={} doesn't exist", registerRequest.country());
            }
        }
    }
}
