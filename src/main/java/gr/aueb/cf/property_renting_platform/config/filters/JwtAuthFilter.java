package gr.aueb.cf.property_renting_platform.config.filters;

import gr.aueb.cf.property_renting_platform.services.JwtService;
import gr.aueb.cf.property_renting_platform.services.MyUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Authenticates requests using the ACCESS token from:
 * Authorization: Bearer <token>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    public static final String AUTH_EXCEPTION_ATTR = "auth_exception";

    private final JwtService jwtService;
    private final MyUserDetailsService userDetailsService;

    /**
     * Processes the request attempting to extract and validate the jwt, granting access in case of a valid token,
     * not granting access and clearing the cookies in case of an invalid one.
     *
     * @param request      the incoming HTTP request
     * @param response     the HTTP response to modify if necessary
     * @param chain  the remaining filter chain to execute
     * @throws ServletException if the filter fails during processing
     * @throws IOException      if I/O errors occur during filtering
     * */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {

        // Already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        String token = header.substring("Bearer ".length()).trim();

        try {
            Jws<Claims> jws = jwtService.parseAndValidate(token);
            String email = jws.getPayload().getSubject();

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            var auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (ExpiredJwtException e) {
            // triggers AuthenticationEntryPoint 401
            throw new AuthenticationCredentialsNotFoundException("Token has expired");
        } catch (JwtException | IllegalArgumentException e) {
            // triggers AuthenticationEntryPoint 401
            throw new BadCredentialsException("Invalid token");
        } catch (BadCredentialsException e) {
            // just leave it to move to the next filter
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during validation", e);
            throw new AuthenticationCredentialsNotFoundException("Token validation failed");
        }
        chain.doFilter(request, response);
    }
}
