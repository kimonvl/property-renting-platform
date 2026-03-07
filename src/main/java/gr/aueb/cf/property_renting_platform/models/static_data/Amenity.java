package gr.aueb.cf.property_renting_platform.models.static_data;

import gr.aueb.cf.property_renting_platform.models.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(
        name = "amenities",
        indexes = {
                @Index(name = "idx_amenities_code", columnList = "code", unique = true)
        }
)
public class Amenity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // e.g. "WIFI", "AIR_CONDITIONING"
    @Column(nullable = false, unique = true, length = 80)
    @EqualsAndHashCode.Include
    private String code;

    // e.g. "Free WiFi"
    @Column(nullable = false, length = 160)
    private String label;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name="group_name", columnDefinition="amenity_group_enum", nullable=false)
    private AmenityGroup groupName;
}
