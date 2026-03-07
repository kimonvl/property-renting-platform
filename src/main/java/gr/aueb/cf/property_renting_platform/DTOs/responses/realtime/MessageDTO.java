package gr.aueb.cf.property_renting_platform.DTOs.responses.realtime;

import java.time.Instant;
import java.util.UUID;

public record MessageDTO(
        UUID chatUuid,
        UUID bookingUuid,
        UUID messageUuid,
        UUID authorUuid,
        String content,
        String photo,
        Instant createdAt
) {}
