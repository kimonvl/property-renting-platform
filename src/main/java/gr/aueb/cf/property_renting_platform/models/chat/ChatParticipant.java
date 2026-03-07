package gr.aueb.cf.property_renting_platform.models.chat;

import gr.aueb.cf.property_renting_platform.models.AbstractEntity;
import gr.aueb.cf.property_renting_platform.models.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "chat_participants",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_chat_participant", columnNames = {"chat_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_chat_participants_user", columnList = "user_id"),
                @Index(name = "idx_chat_participants_chat", columnList = "chat_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ChatParticipant extends AbstractEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Per-user state
    @Column(name = "last_read_at")
    private Instant lastReadAt;

}