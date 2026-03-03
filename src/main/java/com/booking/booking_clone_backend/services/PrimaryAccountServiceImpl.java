package com.booking.booking_clone_backend.services;

import com.booking.booking_clone_backend.DTOs.responses.partner.primary_account.PropertyOperationRowDTO;
import com.booking.booking_clone_backend.DTOs.responses.partner.primary_account.SummaryTileDTO;
import com.booking.booking_clone_backend.constants.MessageConstants;
import com.booking.booking_clone_backend.exceptions.EntityNotFoundException;
import com.booking.booking_clone_backend.mappers.AddressMapper;
import com.booking.booking_clone_backend.models.booking.BookingStatus;
import com.booking.booking_clone_backend.models.property.Property;
import com.booking.booking_clone_backend.models.user.User;
import com.booking.booking_clone_backend.repos.BookingRepo;
import com.booking.booking_clone_backend.repos.PropertyRepo;
import com.booking.booking_clone_backend.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrimaryAccountServiceImpl implements PrimaryAccountService{

    private final UserRepo userRepo;
    private final PropertyRepo propertyRepo;
    private final BookingRepo bookingRepo;
    private final AddressMapper addressMapper;

    @Override
    @PreAuthorize("hasAuthority('VIEW_STATISTICS')")
    @Transactional(readOnly = true)
    public List<PropertyOperationRowDTO> getOperationsTable(String userEmail) throws EntityNotFoundException {
        User owner = null;
        try {
            owner = userRepo.findByEmailIgnoreCase(userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("OperationsTableUser", "User with email " + userEmail + " not found"));

            List<Property> properties = propertyRepo.findByOwner(owner);

            var from = LocalDate.now();
            var to = from.plusDays(2);
            var arrivalsNext48 = bookingRepo.countArrivalsByProperty(
                    owner.getId(),
                    BookingStatus.CONFIRMED,
                    from,
                    to
            );
            for (var item : arrivalsNext48){
                System.out.println(item.getPropertyId());
            }
            var departuresNext48 = bookingRepo.countDeparturesByProperty(
                    owner.getId(),
                    BookingStatus.CONFIRMED,
                    from,
                    to
            );
            List<PropertyOperationRowDTO> propertyOperationTable = extractRows(properties, arrivalsNext48, departuresNext48);

            log.debug("Successfully retrieved operations table for user with email {}: {} entries",
                    userEmail, propertyOperationTable.size());
            return propertyOperationTable;
        } catch (EntityNotFoundException e) {
            log.error("Failed to get operations table for user with email {}: {}",
                    userEmail, e.getMessage());
            throw e;
        }
    }

    private List<PropertyOperationRowDTO> extractRows(List<Property> properties, List<BookingRepo.PropertyCountRow> arrivalsNext48, List<BookingRepo.PropertyCountRow> departuresNext48) {
        List<PropertyOperationRowDTO> propertyOperationTable = new ArrayList<>();
        for (Property property : properties) {
            Long arrivalsCount = 0L;
            Long departuresCount = 0L;
            // Find the property's row of the grouped by property results, if exists
            var arrivals = arrivalsNext48.stream().filter((row) -> row.getPropertyId() == property.getId()).toList();
            var departures = departuresNext48.stream().filter((row) -> row.getPropertyId() == property.getId()).toList();
            if (!arrivals.isEmpty()){
                arrivalsCount = arrivals.getFirst().getCount();
            }
            if (!departures.isEmpty()){
                departuresCount = departures.getFirst().getCount();
            }
            // TODO add guestMessages and bookingMessages
            PropertyOperationRowDTO propertyOperationRowDTO = new PropertyOperationRowDTO(
                    property.getUuid(),
                    property.getName(),
                    addressMapper.toDto(property.getAddress()),
                    property.getStatus(),
                    arrivalsCount,
                    departuresCount,
                    0L,
                    0L
            );
            propertyOperationTable.add(propertyOperationRowDTO);
        }
        return propertyOperationTable;
    }

    @Override
    @PreAuthorize("hasAuthority('VIEW_STATISTICS')")
    @Transactional(readOnly = true)
    public List<SummaryTileDTO> getSummaryTiles(String userEmail) throws EntityNotFoundException {
        User owner = null;
        try {
            owner = userRepo.findByEmailIgnoreCase(userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("SummaryTilesUser", "User with email " + userEmail + " not found"));

            ZoneId zone = ZoneId.of("Europe/Athens");
            Instant fromInstant = LocalDate.now(zone).atStartOfDay(zone).toInstant();
            Instant toInstant = LocalDate.now(zone).plusDays(2).atStartOfDay(zone).toInstant();
            LocalDate today = LocalDate.now(zone);

            List<SummaryTileDTO> tiles = new ArrayList<>();
            tiles.add(new SummaryTileDTO("Reservations", bookingRepo.countReservationsCreatedBetween(
                    owner.getId(),
                    BookingStatus.CONFIRMED,
                    fromInstant,
                    toInstant
            )));
            tiles.add(new SummaryTileDTO("Arrivals", bookingRepo.countArrivalsBetween(
                    owner.getId(),
                    BookingStatus.CONFIRMED,
                    today,
                    today
            )));
            tiles.add(new SummaryTileDTO("Departures", bookingRepo.countDeparturesBetween(
                    owner.getId(),
                    BookingStatus.CONFIRMED,
                    today,
                    today
            )));
            // TODO add reviews and cancellations
            tiles.add(new SummaryTileDTO("Reviews", 0L));
            tiles.add(new SummaryTileDTO("Cancellations", 0L));
            return tiles;
        } catch (EntityNotFoundException e) {
            log.error("Failed to get summary tiles for user with email {}: {}",
                    userEmail, e.getMessage());
            throw e;
        }
    }
}
