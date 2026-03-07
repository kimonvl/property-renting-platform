package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.amenity.AmenitiesDictionaryItemDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.country.CountryDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.language.LanguageDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.role.RoleDTO;

import java.util.List;

public interface DictionaryService {
    List<AmenitiesDictionaryItemDTO> getAmenitiesDictionaryGroupByGroupName();
    List<LanguageDTO> getLanguageDictionary();
    List<CountryDTO> getCountryDictionary();
    List<RoleDTO> getRoleDictionary();

    boolean isCountryExists(String code);
    List<String> findIncorrectAmenityCodes(List<String> codes);
    List<String> findIncorrectLanguageCodes(List<String> codes);
}
