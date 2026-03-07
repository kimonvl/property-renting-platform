package gr.aueb.cf.property_renting_platform.models.availability;

import gr.aueb.cf.property_renting_platform.models.AbstractEntity;
import gr.aueb.cf.property_renting_platform.models.booking.Booking;
import gr.aueb.cf.property_renting_platform.models.property.Property;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(
        name = "property_availability",
        indexes = {
                @Index(name = "idx_availability_property", columnList = "property_id"),
                @Index(name = "idx_availability_dates", columnList = "start_date, end_date")
        }
)
public class PropertyAvailability extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    @Column(unique = true, nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID uuid = UUID.randomUUID();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", unique = true)
    private Booking booking;


    // Use [startDate, endDate) convention (end exclusive) in your service logic
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;
}
