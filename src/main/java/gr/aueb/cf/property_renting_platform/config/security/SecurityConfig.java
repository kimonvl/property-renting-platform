package gr.aueb.cf.property_renting_platform.config.security;

import gr.aueb.cf.property_renting_platform.config.filters.JwtAuthFilter;
import gr.aueb.cf.property_renting_platform.models.user.CapabilityEnum;
import gr.aueb.cf.property_renting_platform.services.MyUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security configuration:
 * - Stateless API (no sessions)
 * - Access token in Authorization header (Bearer ...)
 * - Refresh token in HttpOnly cookie (/auth/*)
 * - 401 when unauthenticated, 403 when authenticated but forbidden (wrong role)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final MyUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Value("${app.cors.allowed-origin}")
    private String allowedOrigin;

    @Bean
    public SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http)
            throws Exception {

        http
                // No server-side sessions. Every request must carry auth (access token).
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // CORS (used by your React app with withCredentials)
                .cors(Customizer.withDefaults())

                // Disable defaults for a pure REST API
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(myCustomAuthenticationEntryPoint())
                        .accessDeniedHandler(myCustomAccessDeniedHandler()))

                .authorizeHttpRequests(auth -> auth
                        // Let Spring's error dispatch happen without security interference
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/error").permitAll()

                        // CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Auth endpoints are public (refresh uses cookie)
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()

                        // Guest endpoints are public
                        .requestMatchers(HttpMethod.GET, "/guest/properties/details/{propertyId}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/guest/properties/search").permitAll()
                        // Partner endpoints require specific capabilities
                        .requestMatchers(HttpMethod.POST, "/partner/properties/create").hasAuthority(CapabilityEnum.CREATE_PROPERTY.name())
                        .requestMatchers(HttpMethod.GET, "/partner/primary-account/operations-table").hasAuthority(CapabilityEnum.VIEW_STATISTICS.name())
                        .requestMatchers(HttpMethod.GET, "/partner/primary-account/summary-tiles").hasAuthority(CapabilityEnum.VIEW_STATISTICS.name())

                        // Booking endpoints
                        .requestMatchers(HttpMethod.GET, "/bookings/{id}/status").hasAuthority(CapabilityEnum.VIEW_BOOKING.name())
                        .requestMatchers(HttpMethod.POST, "/bookings/create").hasAuthority(CapabilityEnum.CREATE_BOOKING.name())
                        .requestMatchers(HttpMethod.POST, "/bookings/delete/{bookingId}").hasAuthority(CapabilityEnum.DELETE_BOOKING.name())

                        // Payment endpoints
                        .requestMatchers(HttpMethod.POST, "/payments/create-intent").hasAuthority(CapabilityEnum.CREATE_PAYMENT.name())

                        // Dictionary endpoints are public
                        .requestMatchers("/dictionaries/**").permitAll()

                        .requestMatchers("/stripe/webhook").permitAll()

                        // Everything else requires being authenticated
                        .anyRequest().authenticated()
                )

                // Email/password auth provider for /auth/login
                .authenticationProvider(authenticationProvider())

                // Validate access JWT on every request
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Authentication provider for username/password login.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Password encoder used when registering + validating login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * AuthenticationManager needed to call authManager.authenticate(...) in AuthService.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS config:
     * - allow your SPA origin
     * - allow credentials (refresh cookie)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // If you ever want wildcard patterns, use setAllowedOriginPatterns instead.
        cfg.setAllowedOrigins(List.of(allowedOrigin));

        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public AuthenticationEntryPoint myCustomAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    public AccessDeniedHandler myCustomAccessDeniedHandler() {
        return new CustomAccessDeniedHandler(objectMapper);
    }
}
