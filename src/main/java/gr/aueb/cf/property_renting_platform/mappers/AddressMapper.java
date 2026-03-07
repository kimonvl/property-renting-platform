package gr.aueb.cf.property_renting_platform.mappers;

import gr.aueb.cf.property_renting_platform.DTOs.responses.property.AddressDTO;
import gr.aueb.cf.property_renting_platform.models.Address;
import gr.aueb.cf.property_renting_platform.models.static_data.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper responsible for converting {@link Address} entities
 * into {@link AddressDTO} objects.
 *
 * <p>This mapper is used to transform property address persistence models
 * into API-facing data transfer objects.</p>
 * */
@Mapper(componentModel = "spring")
public interface AddressMapper {

    /**
     * Converts a {@link Address} into a {@link AddressDTO}.
     *
     * @param address the property address entity to convert
     * @return the mapped user DTO
     * */
    @Mapping(target = "country", source = "country.code")
    @Mapping(target = "postCode", source = "postcode")
    AddressDTO toDto(Address address);

    /**
     * Converts a {@link AddressDTO} into a {@link Address}.
     *
     * @param addressDTO the property address entity to convert
     * @return the mapped user DTO
     * */
    @Mapping(target = "country", source = "country")
    @Mapping(target = "postcode", source = "addressDTO.postCode")
    Address toAddressEntity(AddressDTO addressDTO, Country country);

    /**
     * Converts a List of {@link Address}s into a List of {@link AddressDTO}s.
     *
     * @param addresses the List of property address entities to convert
     * @return the List of mapped user DTOs
     * */
    List<AddressDTO> toDtoList(List<Address> addresses);
}

