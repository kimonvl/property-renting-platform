package gr.aueb.cf.property_renting_platform.DTOs.responses.dictionaries.amenity;

import gr.aueb.cf.property_renting_platform.models.static_data.AmenityGroup;

public record AmenityDTO(long id, String code, String label, AmenityGroup groupName) {
}
