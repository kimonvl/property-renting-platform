package com.booking.booking_clone_backend.repos;

import com.booking.booking_clone_backend.models.chat.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepo extends JpaRepository<Chat, Long> {

    // for both partner and guest
    @Query("""
  select c
  from Chat c
  where c.booking.property.id = :propertyId
  order by c.lastMessageAt desc nulls last, c.id desc
""")
    Page<Chat> findChatsForPropertySortedByLastMessageSent(@Param("propertyId") Long propertyId, Pageable pageable);

    // Guest chat filtering and sorting queries
    @Query("""
  select distinct c
  from Chat c
  join ChatParticipant cp on cp.chat = c
  where cp.user.id = :userId
  order by c.lastMessageAt desc nulls last, c.id desc
""")
    Page<Chat> findChatsSortedByMostRecentMessage(
            @Param("userId") Long userId,
            Pageable pageable
    );

    // Partner chat filtering and sorting queries
    @Query("""
  select c
  from Chat c
  join ChatParticipant cp on cp.chat = c
  join Message m on m.chat = c
  where c.booking.property.id = :propertyId
    and cp.user.id = :ownerUserId
    and m.author.id <> :ownerUserId
    and (cp.lastReadAt is null or m.createdAt > cp.lastReadAt)
  group by c
  order by max(m.createdAt) desc
""")
    Page<Chat> findChatsForPropertySortedByRecentUnseenMessageForOwner(
            @Param("propertyId") Long propertyId,
            @Param("ownerUserId") Long ownerUserId,
            Pageable pageable
    );

    @Query("""
  select distinct c
  from Chat c
  join ChatParticipant ownerCp
    on ownerCp.chat = c and ownerCp.user.id = :ownerUserId
  join ChatParticipant guestCp
    on guestCp.chat = c and guestCp.user.id <> :ownerUserId
  join guestCp.user guest
  where c.booking.property.id = :propertyId
    and lower(guest.email) like lower(concat('%', :q, '%'))
  order by c.lastMessageAt desc nulls last, c.id desc
""")
    Page<Chat> searchChatsForPropertyByGuestEmail(
            @Param("propertyId") Long propertyId,
            @Param("ownerUserId") Long ownerUserId,
            @Param("q") String q,
            Pageable pageable
    );
}
