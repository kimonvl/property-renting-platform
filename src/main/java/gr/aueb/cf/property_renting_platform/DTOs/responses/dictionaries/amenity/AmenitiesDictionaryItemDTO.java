package gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.amenity;

import java.util.List;

public record AmenitiesDictionaryItemDTO(String code, String title, List<AmenityDTO> items) {
}
