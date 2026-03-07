package gr.aueb.cf.property_renting_platform.models.attachment;

import gr.aueb.cf.property_renting_platform.models.property.Property;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Table(name = "property_attachments",
        indexes = {
                @Index(name = "idx_property_attachments_property_id", columnList = "property_id"),
                @Index(name = "idx_property_attachments_url", columnList = "url")
        })
public class PropertyAttachment extends AbstractAttachment {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
}
