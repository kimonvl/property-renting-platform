package com.booking.booking_clone_backend.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

@Slf4j
@Component
public class WebSocketEventsLogger {

    @EventListener
    public void onConnect(SessionConnectEvent event) {
        StompHeaderAccessor h = StompHeaderAccessor.wrap(event.getMessage());
        log.info("SessionConnectEvent session={}", h.getSessionId());
    }

    @EventListener
    public void onConnected(SessionConnectedEvent event) {
        StompHeaderAccessor h = StompHeaderAccessor.wrap(event.getMessage());
        log.info("SessionConnectedEvent session={}", h.getSessionId());
    }

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor h = StompHeaderAccessor.wrap(event.getMessage());
        log.info("SessionSubscribeEvent session={}, user={}, destination={}",
                h.getSessionId(),
                h.getUser() != null ? h.getUser().getName() : "anonymous",
                h.getDestination());
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        log.info("SessionDisconnectEvent session={}, closeStatus={}",
                event.getSessionId(), event.getCloseStatus());
    }
}