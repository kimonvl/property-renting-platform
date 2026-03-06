package com.booking.booking_clone_backend.repos;

import com.booking.booking_clone_backend.models.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepo extends JpaRepository<Message, Long> {
}
