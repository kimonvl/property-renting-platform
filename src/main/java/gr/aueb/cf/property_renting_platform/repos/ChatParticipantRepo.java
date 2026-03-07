package gr.aueb.cf.property_renting_platform.repos;

import gr.aueb.cf.property_renting_platform.models.chat.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepo extends JpaRepository<ChatParticipant, Long> {
}
