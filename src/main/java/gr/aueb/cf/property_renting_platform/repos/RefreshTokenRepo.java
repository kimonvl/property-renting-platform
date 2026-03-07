package gr.aueb.cf.property_renting_platform.repos;

import gr.aueb.cf.property_renting_platform.models.refresh_token.RefreshToken;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<@NonNull RefreshToken, @NonNull Long> {
    Optional<RefreshToken> findByToken(String token);
}
