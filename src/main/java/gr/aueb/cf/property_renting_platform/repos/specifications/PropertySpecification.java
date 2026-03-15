package gr.aueb.cf.property_renting_platform.repos.specifications;

import gr.aueb.cf.property_renting_platform.DTOs.requests.guest.property.PropertySearchRequest;
import gr.aueb.cf.property_renting_platform.DTOs.requests.partner.chat.ChatSearchRequest;
import gr.aueb.cf.property_renting_platform.models.availability.PropertyAvailability;
import gr.aueb.cf.property_renting_platform.models.chat.Chat;
import gr.aueb.cf.property_renting_platform.models.property.PetsPolicy;
import gr.aueb.cf.property_renting_platform.models.property.Property;
import gr.aueb.cf.property_renting_platform.models.property.PropertyStatus;
import gr.aueb.cf.property_renting_platform.models.static_data.Amenity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PropertySpecification {

    public static Specification<Property> build(PropertySearchRequest request) {
        return Specification.allOf(
                //isPublished(),
                cityEqualsIgnoreCase(request.city()),
                allowPets(request.pets()),
                guestsAtLeast(request.maxGuest()),
                bedroomsAtLeast(request.bedroomCount()),
                bathroomsAtLeast(request.bathroomCount()),
                priceBetween(request.minPrice(), request.maxPrice()),
                availableBetween(request.checkIn(), request.checkOut()),
                hasAllAmenities(request.amenities())
    );

    }

    public static Specification<@NonNull Property> isPublished() {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), PropertyStatus.PUBLISHED));
    }

    public static Specification<@NonNull Property> allowPets(Boolean pets) {
        return ((root, query, criteriaBuilder) -> {
            if (pets == null || !pets)
                return criteriaBuilder.conjunction();
            return root.get("petsPolicy").in(PetsPolicy.YES, PetsPolicy.UPON_REQUEST);
        });
    }

    public static Specification<@NonNull Property> cityEqualsIgnoreCase(String city) {
        return ((root, query, criteriaBuilder) -> {
            if (city == null || city.isBlank())
                return criteriaBuilder.conjunction();
            var address = root.join("address");
            return criteriaBuilder.equal(criteriaBuilder.lower(address.get("city")), city.trim().toLowerCase());
        });
    }

    public static Specification<@NonNull Property> guestsAtLeast(Integer guestsMin) {
        return ((root, query, criteriaBuilder) -> {
            if (guestsMin == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.greaterThanOrEqualTo(root.get("maxGuests"), guestsMin);
        });
    }

    public static Specification<@NonNull Property> bedroomsAtLeast(Integer bedroomsMin) {
        return ((root, query, criteriaBuilder) -> {
            if (bedroomsMin == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.greaterThanOrEqualTo(root.get("bedroomCount"), bedroomsMin);
        });
    }

    public static Specification<@NonNull Property> bathroomsAtLeast(Integer bathroomsMin) {
        return ((root, query, criteriaBuilder) -> {
            if (bathroomsMin == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.greaterThanOrEqualTo(root.get("bathrooms"), bathroomsMin);
        });
    }

    public static Specification<@NonNull Property> priceBetween(BigDecimal min, BigDecimal max) {
        return ((root, query, criteriaBuilder) -> {
            if (min == null && max == null)
                return criteriaBuilder.conjunction();
            if (min != null && max != null)
                return criteriaBuilder.between(root.get("pricePerNight"), min, max);
            if (min != null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("pricePerNight"), min);
            return criteriaBuilder.lessThanOrEqualTo(root.get("pricePerNight"), max);
        });
    }

    public static Specification<@NonNull Property> availableBetween(LocalDate checkIn, LocalDate checkOut) {
        return (root, query, cb) -> {
            if (checkIn == null || checkOut == null) return cb.conjunction();

            // NOT EXISTS (select 1 from PropertyAvailability pa where pa.property = root and overlap)
            Subquery<Long> sq = query.subquery(Long.class);
            var pa = sq.from(PropertyAvailability.class);

            // Select 1 block if exists
            sq.select(cb.literal(1L));

            var sameProperty = cb.equal(pa.get("property"), root);

            // overlap for [start, end) convention:
            // pa.start < checkOut AND pa.end > checkIn
            var overlap = cb.and(
                    cb.lessThan(pa.get("startDate"), checkOut),
                    cb.greaterThan(pa.get("endDate"), checkIn)
            );

            sq.where(cb.and(sameProperty, overlap));

            // If subquery returns one result then there is an overlap
            return cb.not(cb.exists(sq));
        };
    }

    /**
     * For each property row in the outer query, it checks:
     *
     * Count distinct amenity codes for that property that are in the selected list
     *
     * Must equal the size of the selected list
     *
     * So if user selects [WIFI, KITCHEN, BALCONY], the property must have all 3.
     *
     * */

    public static Specification<@NonNull Property> hasAllAmenities(List<String> codes) {
        return (root, query, cb) -> {
            if (codes == null || codes.isEmpty()) {
                return cb.conjunction();
            }

            List<String> distinctCodes = codes.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(s -> s.toUpperCase(Locale.ROOT))
                    .distinct()
                    .toList();

            if (distinctCodes.isEmpty()) {
                return cb.conjunction();
            }

            query.distinct(true);

            Subquery<Long> sq = query.subquery(Long.class);
            Root<Property> propertySubRoot = sq.from(Property.class);
            Join<Property, Amenity> amenityJoin = propertySubRoot.join("amenities", JoinType.INNER);

            // countDistinct is defensive here in case a property has an amenity 2 times (bad data)
            // Select the number of amenity codes of a property that :
            sq.select(cb.countDistinct(cb.upper(amenityJoin.get("code"))));
            sq.where(
                    // Subqueries property id reference root properties id
                    cb.equal(propertySubRoot.get("id"), root.get("id")),
                    // And the code is in the list of required amenities
                    cb.upper(amenityJoin.get("code")).in(distinctCodes)
            );

            // Return properties whose subquery returned the same number of results as the distinct codes
            // Meaning that property has all the required amenities and possibly more
            return cb.equal(sq, (long) distinctCodes.size());
        };
    }
}
