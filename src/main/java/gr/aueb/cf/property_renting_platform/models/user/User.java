package gr.aueb.cf.property_renting_platform.models.user;

import gr.aueb.cf.property_renting_platform.models.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "users",
        indexes = @Index(name = "idx_users_email", columnList = "email", unique = true))
public class User extends AbstractEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    @Column(unique = true, nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false, unique = true, length = 320)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "personal_info_id")
    private PersonalInfo personalInfo;

    @Column(nullable = false)
    private boolean enabled = true;

    @NullMarked
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities =  new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        role.getCapabilities()
                .forEach(capability -> grantedAuthorities.add(new SimpleGrantedAuthority(capability.getName())));
        return grantedAuthorities;
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    @NullMarked
    @Override
    public String getUsername() {
        return email;
    }
}
