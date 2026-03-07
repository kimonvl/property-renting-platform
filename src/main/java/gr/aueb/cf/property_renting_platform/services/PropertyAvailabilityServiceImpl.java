package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.property_renting_platform.models.availability.PropertyAvailability;
import gr.aueb.cf.property_renting_platform.models.booking.Booking;
import gr.aueb.cf.property_renting_platform.repos.PropertyAvailabilityRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyAvailabilityServiceImpl implements PropertyAvailabilityService {

    private final PropertyAvailabilityRepo propertyAvailabilityRepo;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void blockDatesForBooking(Booking booking) throws EntityInvalidArgumentException {
        LocalDate checkIn = booking.getCheckInDate();
        LocalDate checkOut = booking.getCheckOutDate();

        validateDates(checkIn, checkOut);

        try {
            PropertyAvailability pa = new PropertyAvailability();
            pa.setBooking(booking);
            pa.setProperty(booking.getProperty());
            pa.setStartDate(checkIn);
            pa.setEndDate(checkOut);

            propertyAvailabilityRepo.save(pa);
            propertyAvailabilityRepo.flush();

        } catch (DataIntegrityViolationException e) {
            if (isExclusionViolation(e)) {
                throw new EntityInvalidArgumentException("BlockDates", "Failed to block dates: selected dates overlap with existing booking. propertyId=" + booking.getProperty().getId() + ", checkIn=" + checkIn + ", checkOut=" + checkOut);
            }
            log.error("Block dates failed: integrity violation. propertyId={}, checkIn={}, checkOut={}",
                    booking.getProperty().getId(), checkIn, checkOut, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public int deleteBlocksByBookingIds(List<Long> bookingIds) {
        return propertyAvailabilityRepo.deleteByBookingIds(bookingIds);
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) throws EntityInvalidArgumentException {
        if (checkIn == null || checkOut == null) {
            throw new EntityInvalidArgumentException("ValidateDates", "Check-in and check-out dates must be provided");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new EntityInvalidArgumentException("ValidateDates", "Check-out date must be after check-in date");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new EntityInvalidArgumentException("ValidateDates", "Check-in date cannot be in the past");
        }
    }

    private boolean isExclusionViolation(DataIntegrityViolationException e) {
        Throwable t = e;
        while (t != null) {
            if (t instanceof ConstraintViolationException cve) {
                return "ex_availability_no_overlap".equals(cve.getConstraintName());
            }
            t = t.getCause();
        }
        return false;
    }
}