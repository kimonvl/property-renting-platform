package gr.aueb.cf.property_renting_platform.models.chat;

import gr.aueb.cf.property_renting_platform.models.AbstractEntity;
import gr.aueb.cf.property_renting_platform.models.booking.Booking;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Collections;
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
        name = "chats",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_chats_booking", columnNames = "booking_id")
        }
)
public class Chat extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    @Column(unique = true, nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID uuid = UUID.randomUUID();

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, updatable = false)
    private Booking booking;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChatParticipant> participants = new HashSet<>();

    public Set<ChatParticipant> getAllParticipants() {
        return participants == null ? Set.of() : Collections.unmodifiableSet(participants);
    }

    public void addParticipant(ChatParticipant participant) {
        if (participants == null) participants = new HashSet<>();
        participants.add(participant);
        participant.setChat(this);
    }

    public void removeParticipant(ChatParticipant participant) {
        if (participants == null) return;
        participants.remove(participant);
        participant.setChat(null);
    }

    @Column(name = "last_message_at", columnDefinition = "TIMESTAMPTZ")
    private Instant lastMessageAt;

//    @Getter(AccessLevel.PROTECTED)
//    @Setter(AccessLevel.PRIVATE)
//    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<Message> messages = new ArrayList<>();
//    public List<Message> getAllMessages() {
//        return messages == null
//                ? List.of()
//                : Collections.unmodifiableList(messages);
//    }
//
//    public void addMessage(Message message) {
//        if (messages == null) messages = new ArrayList<>();
//        messages.add(message);
//        message.setChat(this);
//    }
//
//    public void removeMessage(Message message) {
//        if (messages == null) return;
//        messages.remove(message);
//        message.setChat(null);
//    }
}
