package gr.aueb.cf.property_renting_platform.models.static_data;

import gr.aueb.cf.property_renting_platform.models.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Country extends AbstractEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(length = 2, unique = true)
    private String code; // e.g. "GR"

    @Column(nullable = false, length = 120)
    private String name; // e.g. "Greece"
}
