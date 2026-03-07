package gr.aueb.cf.property_renting_platform.models;

import gr.aueb.cf.property_renting_platform.models.static_data.Country;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(
        name = "addresses",
        indexes = {
                @Index(name = "idx_addresses_city", columnList = "city"),
                @Index(name = "idx_addresses_country", columnList = "country_code"), // ✅ matches SQL
                @Index(name = "idx_addresses_postcode", columnList = "postCode")
        }
)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    @Column(unique = true, nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID uuid = UUID.randomUUID();

    // FK property_addresses.country_code -> countries.code
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_code", referencedColumnName = "code", nullable = false)
    private Country country;

    @Column(nullable = false, length = 120)
    private String city;

    @Column(length = 32)
    private String postcode;

    @Column(nullable = false, length = 200)
    private String street;

    @Column(name = "street_number", length = 32)
    private String streetNumber;

    @Column(name = "floor_number", length = 10)
    private String floorNumber;

    private Double lat;
    private Double lng;
}
