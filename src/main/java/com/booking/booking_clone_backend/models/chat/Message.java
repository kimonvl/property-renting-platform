package com.booking.booking_clone_backend.models.chat;

import com.booking.booking_clone_backend.models.AbstractEntity;
import com.booking.booking_clone_backend.models.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(
        name = "messages",
        indexes = {
                @Index(name = "idx_messages_chat_created", columnList = "chat_id, created_at")
        }
)
public class Message extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    @Column(unique = true, nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID uuid = UUID.randomUUID();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false, updatable = false)
    private Chat chat;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private User author;

    @Column(name = "content", length = 4000)
    private String content;

    // TODO photos table
    @Column(name = "photo", length = 600)
    private String photo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "message_seen_by",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> message_seen_by = new HashSet<>();
}
