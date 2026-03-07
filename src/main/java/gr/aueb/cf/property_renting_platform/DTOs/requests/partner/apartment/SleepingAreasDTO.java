package gr.aueb.cf.property_renting_platform.DTOs.requests.partner.apartment;

import java.util.List;

public record SleepingAreasDTO(
        List<BedroomDTO> bedrooms,
        LivingRoomDTO livingRoom
) {
}
