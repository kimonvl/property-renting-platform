package gr.aueb.cf.property_renting_platform.config.websocket;

import gr.aueb.cf.property_renting_platform.services.JwtService;
import gr.aueb.cf.property_renting_platform.services.MyUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtStompChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final MyUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String authHeader = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Missing or invalid Authorization header for WebSocket CONNECT");
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            throw new AccessDeniedException("Missing JWT token for WebSocket CONNECT");
        }

        try {
            Jws<Claims> jws = jwtService.parseAndValidate(token);
            String email = jws.getPayload().getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            accessor.setUser(authentication);
            return message;
        } catch (JwtException ex) {
            throw new AccessDeniedException("Invalid or expired JWT for WebSocket CONNECT", ex);
        }
    }
}
