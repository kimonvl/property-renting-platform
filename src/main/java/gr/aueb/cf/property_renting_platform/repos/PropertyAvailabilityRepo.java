package gr.aueb.cf.property_renting_platform.repos;

import gr.aueb.cf.property_renting_platform.models.availability.PropertyAvailability;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PropertyAvailabilityRepo extends JpaRepository<@NonNull PropertyAvailability, @NonNull Long> {
    @Query("""
      select (count(pa) = 0)
      from PropertyAvailability pa
      where pa.property.id = :propertyId
        and pa.startDate < :checkOut
        and pa.endDate > :checkIn
    """)
    boolean isAvailable(
            @Param("propertyId") long propertyId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    @Modifying
    @Query("delete from PropertyAvailability pa where pa.booking.id in :bookingIds")
    int deleteByBookingIds(@Param("bookingIds") List<Long> bookingIds);
}
