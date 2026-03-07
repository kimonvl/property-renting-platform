package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.DTOs.requests.guest.property.PropertySearchRequest;
import gr.aueb.cf.property_renting_platform.DTOs.responses.property.PropertyDetailsDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.property.PropertyShortDTO;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface GuestPropertyService {

    Page<@NonNull PropertyShortDTO> search(PropertySearchRequest request);

    PropertyDetailsDTO getPropertyDetails(UUID propertyId) throws EntityNotFoundException;

    boolean isPropertyExists(UUID propertyId);
}
