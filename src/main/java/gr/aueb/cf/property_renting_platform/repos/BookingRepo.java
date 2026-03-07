package gr.aueb.cf.property_renting_platform.repos;

import gr.aueb.cf.property_renting_platform.models.booking.Booking;
import gr.aueb.cf.property_renting_platform.models.booking.BookingStatus;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepo extends JpaRepository<@NonNull Booking, @NonNull Long> {
    interface PropertyCountRow {
        Long getPropertyId();
        Long getCount();
    }

    Optional<Booking> findByPaymentIntentId(String paymentIntentId);
    Optional<Booking> findByUuid(UUID uuid);

    @Query("""
    select b.property.id as propertyId, count(b) as count
    from Booking b
    where b.property.owner.id = :ownerId
      and b.status = :status
      and b.checkInDate >= :from
      and b.checkInDate <= :to
    group by b.property.id
    """)
    List<PropertyCountRow> countArrivalsByProperty(
            @Param("ownerId") long ownerId,
            @Param("status") BookingStatus status,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
    select b.property.id as propertyId, count(b) as count
    from Booking b
    where b.property.owner.id = :ownerId
      and b.status = :status
      and b.checkOutDate >= :from
      and b.checkOutDate <= :to
    group by b.property.id
    """)
    List<PropertyCountRow> countDeparturesByProperty(
            @Param("ownerId") long ownerId,
            @Param("status") BookingStatus status,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    // -------- Reservations (bookings created in window) --------
    @Query("""
    select count(b)
    from Booking b
    where b.property.owner.id = :ownerId
      and b.status = :status
      and b.createdAt >= :from
      and b.createdAt < :to
    """)
    long countReservationsCreatedBetween(
            @Param("ownerId") long ownerId,
            @Param("status") BookingStatus status,
            @Param("from") Instant from,
            @Param("to") Instant to
    );

    // -------- Arrivals (check-ins in date window) --------
    @Query("""
    select count(b)
    from Booking b
    where b.property.owner.id = :ownerId
      and b.status = :status
      and b.checkInDate >= :from
      and b.checkInDate <= :to
    """)
    long countArrivalsBetween(
            @Param("ownerId") long ownerId,
            @Param("status") BookingStatus status,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    // -------- Departures (check-outs in date window) --------
    @Query("""
    select count(b)
    from Booking b
    where b.property.owner.id = :ownerId
      and b.status = :status
      and b.checkOutDate >= :from
      and b.checkOutDate <= :to
    """)
    long countDeparturesBetween(
            @Param("ownerId") long ownerId,
            @Param("status") BookingStatus status,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    // Find ids of expired holds
    @Query("""
        select b.id
        from Booking b
        where b.status = :status
          and b.holdExpiresAt is not null
          and b.holdExpiresAt < :now
    """)
    List<Long> findExpiredPendingIds(@Param("status") BookingStatus status,
                                     @Param("now") Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Booking b
        set b.status = :expiredStatus,
            b.updatedAt = :now
        where b.id in :ids
          and b.status = :pendingStatus
    """)
    int markExpired(@Param("ids") List<Long> ids,
                    @Param("now") Instant now,
                    @Param("pendingStatus") BookingStatus pendingStatus,
                    @Param("expiredStatus") BookingStatus expiredStatus);
}
