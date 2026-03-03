package com.booking.booking_clone_backend.mappers;

import com.booking.booking_clone_backend.DTOs.requests.auth.RegisterRequest;
import com.booking.booking_clone_backend.models.static_data.Country;
import com.booking.booking_clone_backend.models.user.Role;
import com.booking.booking_clone_backend.models.user.User;
import com.booking.booking_clone_backend.DTOs.responses.user.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

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
    @Mapping(target = "country", source = "country.code")
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "email", source = "emailNormalized")
    @Mapping(target = "passwordHash", source = "passwordHash")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "firstName", source = "req.firstName")
    @Mapping(target = "lastName", source = "req.lastName")
    @Mapping(target = "enabled", constant = "true")
    User registerRequestToUser(RegisterRequest req,
                               String emailNormalized,
                               String passwordHash,
                               Country country);
}

