package com.booking.booking_clone_backend.repos;

import com.booking.booking_clone_backend.models.property.Property;
import com.booking.booking_clone_backend.models.user.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyRepo extends JpaRepository<@NonNull Property, @NonNull Long>, JpaSpecificationExecutor<@NonNull Property> {
    List<Property> findByOwner(User owner);
    Optional<Property> findByUuid(@NonNull UUID uuid);

    boolean existsByUuid(UUID propertyId);
}
