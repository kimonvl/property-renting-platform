package gr.aueb.cf.property_renting_platform.mappers;

import gr.aueb.cf.property_renting_platform.DTOs.requests.booking.CheckOutDetailsDTO;
import gr.aueb.cf.property_renting_platform.models.booking.BookingCheckoutDetails;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookingCheckoutDetailsMapper {

    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "bookingId", ignore = true)
    @Mapping(target = "contactEmail", source = "email")
    BookingCheckoutDetails toEntity(CheckOutDetailsDTO dto);

}
