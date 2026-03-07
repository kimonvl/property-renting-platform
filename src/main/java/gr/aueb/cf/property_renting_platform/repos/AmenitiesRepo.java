package gr.aueb.cf.property_renting_platform.repos;

import gr.aueb.cf.property_renting_platform.models.static_data.Amenity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmenitiesRepo extends JpaRepository<@NonNull Amenity, @NonNull Long> {
    List<Amenity> findByCodeIn(List<String> codes);
}
