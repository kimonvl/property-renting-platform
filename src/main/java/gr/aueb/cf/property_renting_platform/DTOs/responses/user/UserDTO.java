package gr.aueb.cf.property_renting_platform.DTOs.responses.user;

import gr.aueb.cf.property_renting_platform.models.user.User;

import java.util.UUID;

/**
 * Data transfer object representing a {@link User}.
 *
 * <p>This DTO is returned by user-related endpoints and contains the user exposed details.</p>
 * */
public record UserDTO (
    UUID id,
    String email,
    Long roleId,
    String firstName,
    String lastName,
    String country
) {

}
