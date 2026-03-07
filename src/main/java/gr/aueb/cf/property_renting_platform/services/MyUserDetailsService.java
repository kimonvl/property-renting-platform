package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    // Repos
    private final UserRepo userRepo;
    /**
     * Loads the user identified by the given username (email)
     *
     * @param username email address of the user
     * @return a fully populated {@link UserDetails} instance
     * @throws UsernameNotFoundException if the user doesn't exist
     * */
    @NonNull
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userRepo.findWithRoleAndCapabilitiesByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + username));
    }
}
