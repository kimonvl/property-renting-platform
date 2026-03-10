package gr.aueb.cf.property_renting_platform.config.security;

import gr.aueb.cf.property_renting_platform.models.user.User;
import gr.aueb.cf.property_renting_platform.repos.PropertyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {


    private final PropertyRepo propertyRepo;

    public boolean isUserOwnerOfProperty(UUID propertyUuid, Authentication authentication) {
        User principal = (User) authentication.getPrincipal();

        if (principal == null) return false;

        return propertyRepo.existsByUuidAndOwner_Uuid(propertyUuid, principal.getUuid());
    }
}
