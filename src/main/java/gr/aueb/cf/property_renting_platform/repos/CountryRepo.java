package gr.aueb.cf.property_renting_platform.repos;

import gr.aueb.cf.property_renting_platform.models.static_data.Country;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepo extends JpaRepository<@NonNull Country, @NonNull Long> {
    Optional<Country> findByCode(String code);

    boolean existsByCode(String code);
}
