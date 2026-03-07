package gr.aueb.cf.property_renting_platform.models.property;

import gr.aueb.cf.property_renting_platform.models.AbstractEntity;
import gr.aueb.cf.property_renting_platform.models.Address;
import gr.aueb.cf.property_renting_platform.models.attachment.PropertyAttachment;
import gr.aueb.cf.property_renting_platform.models.static_data.Amenity;
import gr.aueb.cf.property_renting_platform.models.static_data.Language;
import gr.aueb.cf.property_renting_platform.models.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(
        name = "properties",
        indexes = {
                @Index(name = "idx_properties_owner", columnList = "owner_id"),
                @Index(name = "idx_properties_status", columnList = "status"),
                @Index(name = "idx_properties_type", columnList = "type")
        }
)
public class Property extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    @Column(unique = true, nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID uuid = UUID.randomUUID();

    // Partner that owns the property
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.PROTECTED)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "properties_amenities",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();
    public Set<Amenity> getAllAmenities() {
        return amenities == null
                ? Set.of()
                : Collections.unmodifiableSet(amenities);
    }
    public void addAmenity(Amenity amenity) {
        if (amenities == null) amenities = new HashSet<>();
        amenities.add(amenity);
    }
    public void removeAmenity(Amenity amenity) {
        if (amenities == null) return;
        amenities.remove(amenity);
    }

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.PROTECTED)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "properties_languages",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private Set<Language> languages = new HashSet<>();
    public Set<Language> getAllLanguages() {
        return languages == null
                ? Set.of()
                : Collections.unmodifiableSet(languages);
    }
    public void addLanguage(Language language) {
        if (languages == null) languages = new HashSet<>();
        languages.add(language);
    }
    public void removeLanguage(Language language) {
        if (languages == null) return;
        languages.remove(language);
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", columnDefinition = "property_type_enum", nullable = false)
    private PropertyType type = PropertyType.APARTMENT;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "property_status_enum", nullable = false)
    private PropertyStatus status = PropertyStatus.DRAFT;

    @Column(nullable = false, length = 200)
    private String name;

    // Pricing
    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "currency", columnDefinition = "currency_code_enum", nullable = false)
    private CurrencyCode currency = CurrencyCode.EUR;

    // Capacity / details
    @Column
    private Integer maxGuests;

    @Column
    private Integer bathrooms;

    @Column(precision = 10, scale = 2)
    private BigDecimal sizeSqm;

    @Column(nullable = false)
    private Boolean childrenAllowed = true;

    @Column(nullable = false)
    private Boolean cotsOffered = false;

    // Services
    @Column(nullable = false)
    private Boolean breakfastServed = false;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "parking_policy", columnDefinition = "parking_policy_enum", nullable = false)
    private ParkingPolicy parkingPolicy = ParkingPolicy.NONE;

    // Rules
    @Column(nullable = false)
    private Boolean smokingAllowed = false;

    @Column(nullable = false)
    private Boolean partiesAllowed = false;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "pets_policy", columnDefinition = "pets_policy_enum", nullable = false)
    private PetsPolicy petsPolicy = PetsPolicy.NO;

    // Optional check-in/out windows (can be null until filled)
    private LocalTime checkInFrom;
    private LocalTime checkInUntil;
    private LocalTime checkOutFrom;
    private LocalTime checkOutUntil;

    // Sleeping areas: store JSON as text for MVP (works on Postgres).
    // Later you can migrate to jsonb column definition if you want.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sleeping_areas_json", columnDefinition = "jsonb")
    private String sleepingAreasJson;
    @Column(nullable = false)
    private Integer livingRoomCount;
    @Column(nullable = false)
    private Integer bedroomCount;
    @Column(nullable = false)
    private Integer bedCount;
    @Column(nullable = false)
    private String bedSummary;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PropertyAttachment> attachments = new HashSet<>();

    public Set<PropertyAttachment> getAllAttachments() {
        return attachments == null ? Set.of() : Collections.unmodifiableSet(attachments);
    }

    public void addAttachment(PropertyAttachment attachment) {
        if (attachment == null) return;
        if (attachments == null) attachments = new HashSet<>();
        attachments.add(attachment);
        attachment.setProperty(this);
    }

    public void removeAttachment(PropertyAttachment attachment) {
        if (attachments == null) return;
        attachments.remove(attachment);
        if (attachment.getProperty() == this) {
            attachment.setProperty(null);
        }
    }

    @Column(name = "main_photo_id")
    private Long mainPhotoId;

    @Column(name = "main_photo_url")
    private String mainPhotoUrl;
}
