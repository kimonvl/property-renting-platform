package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.DTOs.requests.auth.LoginRequest;
import gr.aueb.cf.property_renting_platform.DTOs.requests.auth.RegisterRequest;
import gr.aueb.cf.property_renting_platform.DTOs.responses.user.UserDTO;
import gr.aueb.cf.property_renting_platform.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.property_renting_platform.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;
import gr.aueb.cf.property_renting_platform.exceptions.InternalErrorException;
import gr.aueb.cf.property_renting_platform.mappers.UserMapper;
import gr.aueb.cf.property_renting_platform.models.static_data.Country;
import gr.aueb.cf.property_renting_platform.models.refresh_token.RefreshToken;
import gr.aueb.cf.property_renting_platform.models.user.Role;
import gr.aueb.cf.property_renting_platform.models.user.User;
import gr.aueb.cf.property_renting_platform.repos.CountryRepo;
import gr.aueb.cf.property_renting_platform.repos.RefreshTokenRepo;
import gr.aueb.cf.property_renting_platform.repos.RoleRepo;
import gr.aueb.cf.property_renting_platform.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final CountryRepo countryRepo;
    private final UserMapper userMapper;
    private final RefreshTokenRepo refreshRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Value("${app.jwt.refresh-days}")
    private long refreshDays;
    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional(rollbackFor = {EntityAlreadyExistsException.class, EntityInvalidArgumentException.class})
    public UserDTO register(RegisterRequest req) throws EntityAlreadyExistsException, EntityInvalidArgumentException {
        try {
            String normalized = req.email().trim().toLowerCase();
            if (userRepo.existsByEmailIgnoreCase(normalized)) {
                throw new EntityAlreadyExistsException("RegisterUser", "User with email=" + normalized + " already exists");
            }
            Country country = countryRepo.findByCode(req.country())
                    .orElseThrow(() -> new EntityInvalidArgumentException("RegisterCountry", "Country code=" + req.country() + " invalid"));

            Role role = roleRepo.findById(req.roleId())
                    .orElseThrow(() -> new EntityInvalidArgumentException("RegisterRole", "Role id=" + req.roleId() + " invalid"));
            User u = userMapper.registerRequestToUser(req, normalized, passwordEncoder.encode(req.password()), country);
            role.addUser(u);

            log.info("User with email={} registered successfully", normalized);
            return userMapper.toDto(userRepo.save(u));
        } catch (EntityAlreadyExistsException e) {
            log.error("Registration failed for email={}. Email already exists", req.email(), e);
            throw e;
        } catch (EntityInvalidArgumentException e) {
            log.error("Registration failed for email={}. Message={}", req.email(), e.getMessage(), e);
            throw e;
        }
    }
    @Override
    @Transactional(rollbackFor = {AuthenticationException.class, EntityInvalidArgumentException.class, InternalErrorException.class})
    public AuthResult login(LoginRequest request) throws EntityInvalidArgumentException, InternalErrorException, EntityNotFoundException {
        try {
            var auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            var principal = (User) auth.getPrincipal();
            if (principal == null) {
                log.error("Login failed for email={}, principal not found", request.email());
                throw new InternalErrorException("Login", "Login failed for email=" + request.email() + " due to unexpected system error");
            }

            if (!principal.getRole().getId().equals(request.roleId())) {
                log.error("Login failed for email={}, role={}", request.email(), request.roleId());
                throw new EntityInvalidArgumentException("LoginRole", "Login failed. Role mismatch for email=" + request.email());
            }
            String access = jwtService.generateAccessToken(
                    principal.getId(),
                    principal.getUsername(),
                    principal.getRole().getName()
            );

            RefreshToken refresh = issueRefreshToken(principal.getUsername());
            log.info("Login succeeded: user with email={} authenticated", request.email());
            return new AuthResult(access, refresh.getToken(), userMapper.toDto(principal));
        } catch (AuthenticationException e) {
            log.warn("Login failed: bad credentials email={}", request.email(), e);
            throw e;
        } catch (EntityInvalidArgumentException e) {
            log.warn("Login failed: role mismatch for email={}", request.email(), e);
            throw e;
        } catch (InternalErrorException e) {
            log.error("Login failed: unexpected system error for email={}", request.email(), e);
            throw e;
        }
    }
    @Override
    @Transactional(rollbackFor = {EntityNotFoundException.class, EntityInvalidArgumentException.class})
    public AuthResult refresh(String refreshTokenValue)
            throws EntityNotFoundException, EntityInvalidArgumentException
    {
        try {
            RefreshToken existing = refreshRepo.findByToken(refreshTokenValue)
                    .orElseThrow(() -> new EntityNotFoundException("RefreshToken", "Refresh token not found"));

            if (existing.isRevoked() || existing.getExpiresAt().isBefore(Instant.now())) {
                throw new EntityInvalidArgumentException("RefreshToken", "Refresh token is expired or revoked");
            }

            // rotate refresh token
            existing.setRevoked(true);
            refreshRepo.save(existing);

            User user = existing.getUser();
            String access = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole().getName());
            RefreshToken newRefresh = issueRefreshToken(user.getEmail());

            log.info("Refresh and access token issued for user with email={}", user.getEmail());
            return new AuthResult(access, newRefresh.getToken(), userMapper.toDto(user));
        } catch (EntityNotFoundException e) {
            log.warn("Refresh failed: Refresh token not found", e);
            throw e;
        } catch (EntityInvalidArgumentException e) {
            log.warn("Refresh failed: Refresh token is expired", e);
            throw e;
        }
    }
    @Override
    @Transactional(rollbackFor = {EntityNotFoundException.class})
    public void logout(String refreshTokenValue) throws EntityNotFoundException {
        try {
            RefreshToken token = refreshRepo.findByToken(refreshTokenValue)
                    .orElseThrow(() -> new EntityNotFoundException("LogoutToken", "Refresh token not found"));

            token.setRevoked(true);
            refreshRepo.save(token);
            log.info("Logout succeeded: User with email={} logged out", token.getUser().getEmail());
        } catch (EntityNotFoundException e) {
            log.warn("Logout failed: Refresh token not found", e);
            throw e;
        }
    }

    private RefreshToken issueRefreshToken(String email) throws  EntityNotFoundException{
        try {
            User user = userRepo.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new EntityNotFoundException("IssueRefreshUser", "User not found for email=" + email));

            byte[] bytes = new byte[32];
            random.nextBytes(bytes);
            String token = HexFormat.of().formatHex(bytes);

            RefreshToken rt = new RefreshToken();
            rt.setUser(user);
            rt.setToken(token);
            rt.setRevoked(false);
            rt.setExpiresAt(Instant.now().plus(refreshDays, ChronoUnit.DAYS));
            log.info("Issue refresh token succeeded for user with email={}", email);
            return refreshRepo.save(rt);
        } catch (EntityNotFoundException e) {
            log.error("Issue refresh token failed: user not found for email={}", email);
            throw e;
        }
    }

    public boolean isUserExists(String email) {
        return userRepo.existsByEmailIgnoreCase(email);
    }

    public record AuthResult(String accessToken, String refreshToken, UserDTO userDTO) {}
}
