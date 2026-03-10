package gr.aueb.cf.property_renting_platform.mappers;

import gr.aueb.cf.property_renting_platform.DTOs.requests.auth.RegisterRequest;
import gr.aueb.cf.property_renting_platform.models.static_data.Country;
import gr.aueb.cf.property_renting_platform.models.user.User;
import gr.aueb.cf.property_renting_platform.DTOs.responses.user.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

/**
 * Mapper responsible for converting {@link User} entities
 * into {@link UserDTO} objects.
 *
 * <p>This mapper is used to transform user persistence models
 * into API-facing data transfer objects.</p>
 * */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UserMapper {

    /**
     * Converts a {@link User} into a {@link UserDTO}.
     *
     * @param user the user entity to convert
     * @return the mapped user DTO
     * */
    @Mapping(target = "firstName", source = "personalInfo.firstName")
    @Mapping(target = "lastName", source = "personalInfo.lastName")
    @Mapping(target = "country", source = "personalInfo.country.code")
    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "id", source = "uuid")
    UserDTO toDto(User user);

    /**
     * Converts a List of {@link User}s into a List of {@link UserDTO}s.
     *
     * @param users the List of user entities to convert
     * @return the List of mapped user DTOs
     * */
    @Mapping(target = "country", source = "country.code")
    List<UserDTO> toDtoList(List<User> users);

    Set<UserDTO> toDtoSet(Set<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "email", source = "emailNormalized")
    @Mapping(target = "passwordHash", source = "passwordHash")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "personalInfo.id", ignore = true)
    @Mapping(target = "personalInfo.country", source = "country")
    @Mapping(target = "personalInfo.firstName", source = "req.firstName")
    @Mapping(target = "personalInfo.lastName", source = "req.lastName")
    User registerRequestToUser(RegisterRequest req,
                               String emailNormalized,
                               String passwordHash,
                               Country country);
}

