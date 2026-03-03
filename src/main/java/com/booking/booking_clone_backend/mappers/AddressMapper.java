package com.booking.booking_clone_backend.mappers;

import com.booking.booking_clone_backend.DTOs.responses.property.AddressDTO;
import com.booking.booking_clone_backend.models.property.PropertyAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper responsible for converting {@link PropertyAddress} entities
 * into {@link AddressDTO} objects.
 *
 * <p>This mapper is used to transform property address persistence models
 * into API-facing data transfer objects.</p>
 * */
@Mapper(componentModel = "spring")
public interface AddressMapper {

    /**
     * Converts a {@link PropertyAddress} into a {@link AddressDTO}.
     *
     * @param address the property address entity to convert
     * @return the mapped user DTO
     * */
    @Mapping(target = "country", source = "country.code")
    @Mapping(target = "postCode", source = "postcode")
    @Mapping(target = "propertyId", source = "uuid")
    AddressDTO toDto(PropertyAddress address);

    /**
     * Converts a List of {@link PropertyAddress}s into a List of {@link AddressDTO}s.
     *
     * @param addresses the List of property address entities to convert
     * @return the List of mapped user DTOs
     * */
    List<AddressDTO> toDtoList(List<PropertyAddress> addresses);
}

