package gr.aueb.cf.property_renting_platform.repos;

import gr.aueb.cf.property_renting_platform.models.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
        Role findByName(String name);
}
