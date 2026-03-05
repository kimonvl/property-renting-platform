package com.booking.booking_clone_backend.repos;

import com.booking.booking_clone_backend.DTOs.responses.review.ReviewDTO;
import com.booking.booking_clone_backend.DTOs.responses.review.ReviewSummaryDTO;
import com.booking.booking_clone_backend.models.review.Review;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ReviewRepo extends JpaRepository<@NonNull Review, @NonNull Long> {

    @Query("""
    select new com.booking.booking_clone_backend.DTOs.responses.review.ReviewSummaryDTO(
      coalesce(avg(r.rating), 0),
      count(r)
    )
    from Review r
    where r.property.id = :propertyId
    """)
    ReviewSummaryDTO getSummaryByPropertyId(@Param("propertyId") long propertyId);

    @Query(
            value = """
      select new com.booking.booking_clone_backend.DTOs.responses.review.ReviewDTO(
          r.uuid, r.rating, r.positiveComment, r.negativeComment,
          r.ownerResponse, r.createdAt, r.ownerRespondedAt,
          new com.booking.booking_clone_backend.DTOs.responses.user.UserDTO(
            g.uuid,
            g.email,
            g.role.id,
            g.personalInfo.firstName,
            g.personalInfo.lastName,
            g.personalInfo.country.code)
      )
      from Review r
      join r.guest g
      where r.property.id = :propertyId
      order by r.createdAt desc
  """,
            countQuery = """
      select count(r)
      from Review r
      where r.property.id = :propertyId
  """
    )
    Page<@NonNull ReviewDTO> findByPropertyIdRecentFirst(@Param("propertyId") long propertyId, Pageable pageable);

    public record PropertySummaryRow(long propertyId, double avgRating, long reviewCount) {}

    @Query("""
        select
          r.property.id as propertyId,
          coalesce(avg(r.rating), 0) as avgRating,
          count(r) as reviewCount
        from Review r
        where r.property.id in :propertyIds
        group by r.property.id
    """)
    List<PropertySummaryRow> getSummariesForProperties(@Param("propertyIds") List<Long> propertyIds);

    default Map<Long, ReviewSummaryDTO> getSummaryMap(List<Long> propertyIds) {
        var rows = getSummariesForProperties(propertyIds);
        Map<Long, ReviewSummaryDTO> map = new HashMap<>(rows.size());
        for (var r : rows) {
            map.put(r.propertyId(), new ReviewSummaryDTO(r.avgRating(), r.reviewCount()));
        }
        return map;
    }


}
