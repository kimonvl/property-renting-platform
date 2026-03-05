package com.booking.booking_clone_backend.models.property;

import com.booking.booking_clone_backend.models.AbstractEntity;
import com.booking.booking_clone_backend.models.static_data.Amenity;
import com.booking.booking_clone_backend.models.user.User;
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

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PropertyLanguage> propertyLanguages = new HashSet<>();
    public Set<PropertyLanguage> getAllPropertyLanguages() {
        return propertyLanguages == null
                ? Set.of()
                : Collections.unmodifiableSet(propertyLanguages);
    }
    public void addPropertyLanguage(PropertyLanguage propertyLanguage) {
        if (propertyLanguages == null) propertyLanguages = new HashSet<>();
        propertyLanguages.add(propertyLanguage);
        propertyLanguage.setProperty(this);
    }
    public void removePropertyLanguage(PropertyLanguage propertyLanguage) {
        if (propertyLanguages == null) return;
        propertyLanguages.remove(propertyLanguage);
        propertyLanguage.setProperty(null);
    }

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PropertyAddress address;

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
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PropertyPhoto> propertyPhotos = new ArrayList<>();
    public List<PropertyPhoto> getAllPropertyPhotos() {
        return propertyPhotos == null
                ? List.of()
                : Collections.unmodifiableList(propertyPhotos);
    }
    public void addPropertyPhoto(PropertyPhoto propertyPhoto) {
        if (propertyPhotos == null) propertyPhotos = new ArrayList<>();
        propertyPhotos.add(propertyPhoto);
        propertyPhoto.setProperty(this);
    }
    public void removePropertyPhoto(PropertyPhoto propertyPhoto) {
        if (propertyPhotos == null) return;
        propertyPhotos.remove(propertyPhoto);
        propertyPhoto.setProperty(null);
    }

    @Column(name = "main_photo_id")
    private Long mainPhotoId;

    @Column(name = "main_photo_url")
    private String mainPhotoUrl;
}
