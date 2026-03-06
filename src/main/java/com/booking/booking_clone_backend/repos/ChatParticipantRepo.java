package com.booking.booking_clone_backend.repos;

import com.booking.booking_clone_backend.models.chat.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepo extends JpaRepository<ChatParticipant, Long> {
}
