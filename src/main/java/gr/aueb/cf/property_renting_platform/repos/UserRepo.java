package gr.aueb.cf.property_renting_platform.repos;

import gr.aueb.cf.property_renting_platform.models.user.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<@NonNull  User, @NonNull Long> {
    Optional<User> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"role", "role.capabilities"})
    Optional<User> findWithRoleAndCapabilitiesByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
