package gr.aueb.cf.property_renting_platform.DTOs.requests.partner.apartment;

import java.util.Map;

public record LivingRoomDTO(Map<BedType, Integer> beds) {
}
