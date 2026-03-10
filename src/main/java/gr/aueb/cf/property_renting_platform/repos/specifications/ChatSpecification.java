package gr.aueb.cf.property_renting_platform.repos.specifications;

import gr.aueb.cf.property_renting_platform.DTOs.requests.partner.chat.ChatSearchRequest;
import gr.aueb.cf.property_renting_platform.models.chat.Chat;
import gr.aueb.cf.property_renting_platform.models.chat.ChatParticipant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;
import java.util.UUID;

public class ChatSpecification {

    public static Specification<Chat> build(ChatSearchRequest filters, Long currentUserId) {
        return Specification.allOf(
                        hasPropertyId(filters.propertyId()),
                        hasFirstNameOrLastNameOrBookingId(filters.searchTerm(), currentUserId),
                        hasUnreadMessagesOnly(filters.isUnreadMessagesOnly(), currentUserId)
                )
                .and(sortByLastMessageAtDesc());
    }

    private static Specification<Chat> hasPropertyId(UUID propertyId) {
        return (root, query, cb) -> propertyId == null
                ? cb.conjunction()
                : cb.equal(root.get("booking").get("property").get("uuid"), propertyId);
    }

    private static Specification<Chat> hasFirstNameOrLastNameOrBookingId(String term, Long currentUserId) {
        return (root, query, cb) -> {
            if (term == null || term.isBlank()) return cb.conjunction();

            String normalized = term.trim();
            if (normalized.chars().allMatch(Character::isDigit)) {
                return cb.like(root.get("booking").get("id").as(String.class), "%" + normalized + "%");
            }

            if (!containsAnyLetter(normalized)) return cb.conjunction();

            query.distinct(true);
            Join<Chat, ChatParticipant> otherCp = root.join("participants", JoinType.INNER);
            Join<Object, Object> otherUser = otherCp.join("user", JoinType.LEFT);
            Join<Object, Object> pi = otherUser.join("personalInfo", JoinType.LEFT);

            String likeTerm = "%" + normalized.toLowerCase(Locale.ROOT) + "%";
            var firstNameLike = cb.like(cb.lower(cb.coalesce(pi.get("firstName"), "")), likeTerm);
            var lastNameLike = cb.like(cb.lower(cb.coalesce(pi.get("lastName"), "")), likeTerm);
            var namePredicate = cb.or(firstNameLike, lastNameLike);

            if (currentUserId == null) return namePredicate;

            return cb.and(
                    cb.notEqual(otherCp.get("user").get("id"), currentUserId),
                    namePredicate
            );
        };
    }

    private static Specification<Chat> hasUnreadMessagesOnly(Boolean isUnreadMessagesOnly, Long currentUserId) {
        return (root, query, cb) -> {
            if (!Boolean.TRUE.equals(isUnreadMessagesOnly) || currentUserId == null) return cb.conjunction();

            query.distinct(true);
            Join<Chat, ChatParticipant> currentCp = root.join("participants", JoinType.INNER);
            currentCp.on(cb.equal(currentCp.get("user").get("id"), currentUserId));
            var hasLatestMessage = cb.isNotNull(root.get("lastMessageAt"));
            var latestFromOtherUser = cb.notEqual(root.get("lastMessageAuthorId"), currentUserId);
            var hasUnreadByTimestamp = cb.or(
                    // lastMessageAt exists here, so if lastReadAt is null then it's unread
                    cb.isNull(currentCp.get("lastReadAt")),
                    cb.greaterThan(root.get("lastMessageAt"), currentCp.get("lastReadAt"))
            );

            return cb.and(hasLatestMessage, latestFromOtherUser, hasUnreadByTimestamp);
        };
    }

    private static Specification<Chat> sortByLastMessageAtDesc() {
        return (root, query, cb) -> {
            if (!isCountQuery(query.getResultType())) {
                query.orderBy(
                        cb.desc(root.get("lastMessageAt")),
                        cb.desc(root.get("id"))
                );
            }
            return cb.conjunction();
        };
    }

    private static boolean isCountQuery(Class<?> resultType) {
        return resultType == Long.class || resultType == long.class;
    }

    private static boolean containsAnyLetter(String value) {
        return value.chars().anyMatch(Character::isLetter);
    }
}
