package gr.aueb.cf.property_renting_platform.repos;

import gr.aueb.cf.property_renting_platform.models.property.Property;
import gr.aueb.cf.property_renting_platform.models.user.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyRepo extends JpaRepository<@NonNull Property, @NonNull Long>, JpaSpecificationExecutor<@NonNull Property> {
    List<Property> findByOwner(User owner);

    //@EntityGraph(attributePaths = {"attachments"})
    Optional<Property> findByUuid(@NonNull UUID uuid);

    boolean existsByUuid(UUID propertyId);

    @EntityGraph(attributePaths = {"address", "amenities"})
    @Query("select distinct p from Property p where p.id in :ids")
    List<Property> findAllByIdInWithAddressAndAmenities(@Param("ids") List<Long> ids);
}
