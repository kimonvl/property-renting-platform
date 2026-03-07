package gr.aueb.cf.property_renting_platform.models.review;

import gr.aueb.cf.property_renting_platform.models.AbstractEntity;
import gr.aueb.cf.property_renting_platform.models.booking.Booking;
import gr.aueb.cf.property_renting_platform.models.property.Property;
import gr.aueb.cf.property_renting_platform.models.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(
        name = "reviews",
        indexes = {
                @Index(name = "idx_reviews_property_created", columnList = "property_id, created_at"),
                @Index(name = "idx_reviews_guest_created", columnList = "guest_id, created_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_reviews_booking", columnNames = "booking_id")
        }
)
public class Review extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    @Column(unique = true, nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID uuid = UUID.randomUUID();

    // Enforce: one review per booking
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, updatable = false)
    private Booking booking;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false, updatable = false)
    private Property property;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false, updatable = false)
    private User guest;

    @Column(nullable = false)
    private int rating; // decide 1-10 or 1-5

    @Column(name = "positive_comment", length = 1500)
    private String positiveComment;

    @Column(name = "negative_comment", length = 1500)
    private String negativeComment;

    // Owner/partner response (manager reply)
    @Column(name = "owner_response", length = 1500)
    private String ownerResponse;

    @Column(name = "owner_responded_at")
    private Instant ownerRespondedAt;

}
