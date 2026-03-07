package gr.aueb.cf.property_renting_platform.models.booking;

import gr.aueb.cf.property_renting_platform.models.AbstractEntity;
import gr.aueb.cf.property_renting_platform.models.property.Property;
import gr.aueb.cf.property_renting_platform.models.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(
        name = "bookings",
        indexes = {
                @Index(name = "idx_bookings_property_checkin", columnList = "property_id, check_in_date"),
                @Index(name = "idx_bookings_property_checkout", columnList = "property_id, check_out_date"),
                @Index(name = "idx_bookings_guest", columnList = "guest_id"),
                @Index(name = "idx_bookings_status", columnList = "status")
        }
)
public class Booking extends AbstractEntity {

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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "guest_count", nullable = false)
    private int guestCount = 1;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "booking_status_enum", nullable = false)
    private BookingStatus status = BookingStatus.CONFIRMED;


    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private BookingCheckoutDetails checkoutDetails;

    public void setCheckoutDetails(BookingCheckoutDetails details) {
        // detach old
        if (this.checkoutDetails != null) {
            this.checkoutDetails.setBooking(null);
        }
        this.checkoutDetails = details;
        // attach new
        if (details != null) {
            details.setBooking(this); // owning side
        }
    }
    // add fields

    @Column(name = "payment_intent_id", length = 120)
    private String paymentIntentId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "payment_status", columnDefinition = "payment_status_enum", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.REQUIRES_PAYMENT;

    @Column(name = "amount_total", precision = 10, scale = 2)
    private BigDecimal amountTotal;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "hold_expires_at")
    private Instant holdExpiresAt;
}
