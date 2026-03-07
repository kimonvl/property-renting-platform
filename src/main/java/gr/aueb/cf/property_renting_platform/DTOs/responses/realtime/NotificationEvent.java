package gr.aueb.cf.property_renting_platform.DTOs.responses.realtime;

import java.time.Instant;

public record NotificationEvent(
        NotificationType type,
        String title,
        String body,
        Object payload,
        Instant createdAt
) {}
