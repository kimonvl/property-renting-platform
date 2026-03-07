package gr.aueb.cf.property_renting_platform.mappers;

import gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.amenity.AmenityDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.country.CountryDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.language.LanguageDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.role.RoleDTO;
import gr.aueb.cf.property_renting_platform.models.static_data.Amenity;
import gr.aueb.cf.property_renting_platform.models.static_data.Language;
import gr.aueb.cf.property_renting_platform.models.static_data.Country;
import gr.aueb.cf.property_renting_platform.models.user.Role;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface DictionaryMapper {
    List<AmenityDTO> amenitiesToDtoList(List<Amenity> amenities);
    Set<AmenityDTO> amenitiesToDtoSet(Set<Amenity> amenities);
    AmenityDTO amenityToDto(Amenity amenity);
    List<LanguageDTO> languagesToDtoList(List<Language> languages);
    List<CountryDTO> countriesToDtoList(List<Country> countries);
    List<RoleDTO> rolesToDtoList(List<Role> roles);
}
