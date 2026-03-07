package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.amenity.AmenitiesDictionaryItemDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.amenity.AmenityDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.country.CountryDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.language.LanguageDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.role.RoleDTO;
import gr.aueb.cf.property_renting_platform.mappers.DictionaryMapper;
import gr.aueb.cf.property_renting_platform.models.static_data.Amenity;
import gr.aueb.cf.property_renting_platform.models.static_data.AmenityGroup;
import gr.aueb.cf.property_renting_platform.models.static_data.Language;
import gr.aueb.cf.property_renting_platform.models.static_data.Country;
import gr.aueb.cf.property_renting_platform.models.user.Role;
import gr.aueb.cf.property_renting_platform.repos.AmenitiesRepo;
import gr.aueb.cf.property_renting_platform.repos.CountryRepo;
import gr.aueb.cf.property_renting_platform.repos.LanguageRepo;
import gr.aueb.cf.property_renting_platform.repos.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService{

    private final AmenitiesRepo amenitiesRepo;
    private final LanguageRepo languageRepo;
    private final CountryRepo countryRepo;
    private final DictionaryMapper dictionaryMapper;
    private final RoleRepo roleRepo;

    @Override
    @Transactional(readOnly = true)
    public List<AmenitiesDictionaryItemDTO> getAmenitiesDictionaryGroupByGroupName() {
        List<AmenitiesDictionaryItemDTO> result = new ArrayList<>();
        List<Amenity> amenities = amenitiesRepo.findAll();
        //improve to one query and group by group name in java
        for(AmenityGroup group : AmenityGroup.values()){
            List<Amenity> amenitiesByGroup = amenities.stream()
                    .filter(amenity -> amenity.getGroupName().equals(group))
                    .toList();
            List<AmenityDTO> amenityDTOs = dictionaryMapper.amenitiesToDtoList(amenitiesByGroup);
            result.add(new AmenitiesDictionaryItemDTO(group.name(), group.getLabel(), amenityDTOs));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LanguageDTO> getLanguageDictionary() {
        List<Language> languages = languageRepo.findAll();
        return dictionaryMapper.languagesToDtoList(languages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountryDTO> getCountryDictionary() {
        List<Country> countries = countryRepo.findAll();
        return dictionaryMapper.countriesToDtoList(countries);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getRoleDictionary() {
        List<Role> roles = roleRepo.findAll();
        return dictionaryMapper.rolesToDtoList(roles);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCountryExists(String code) {
        return countryRepo.existsByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findIncorrectAmenityCodes(List<String> codes) {
        List<Amenity> existing = amenitiesRepo.findByCodeIn(codes);
        Set<Amenity> existingSet = new HashSet<>(existing);

        return codes.stream()
                .filter(code -> {
                    Amenity amenity = new Amenity();
                    amenity.setCode(code);
                    return !existingSet.contains(amenity);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findIncorrectLanguageCodes(List<String> codes) {
        List<Language> existing = languageRepo.findByCodeInIgnoreCase(codes);
        Set<Language> existingSet = new HashSet<>(existing);

        return codes.stream()
                .filter(code -> {
                    Language language = new Language();
                    language.setCode(code);
                    return !existingSet.contains(language);
                })
                .toList();
    }

}
