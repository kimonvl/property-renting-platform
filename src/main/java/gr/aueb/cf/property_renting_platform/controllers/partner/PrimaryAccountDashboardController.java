package gr.aueb.cf.property_renting_platform.controllers.partner;

import gr.aueb.cf.property_renting_platform.DTOs.responses.GenericResponse;
import gr.aueb.cf.property_renting_platform.DTOs.responses.partner.primary_account.PropertyOperationRowDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.partner.primary_account.SummaryTileDTO;
import gr.aueb.cf.property_renting_platform.constants.MessageConstants;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;
import gr.aueb.cf.property_renting_platform.models.user.User;
import gr.aueb.cf.property_renting_platform.services.PrimaryAccountService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partner/primary-account")
@RequiredArgsConstructor
public class PrimaryAccountDashboardController {
    private final PrimaryAccountService primaryAccountService;

    @GetMapping("/operations-table")
    public ResponseEntity<@NonNull GenericResponse<List<PropertyOperationRowDTO>>> getOperationsTable(
            @AuthenticationPrincipal User principal
    ) throws EntityNotFoundException {
        return new ResponseEntity<>(
                new GenericResponse<>(
                        primaryAccountService.getOperationsTable(principal.getUsername()),
                        "GetOperationsTableSucceed",
                        MessageConstants.OPERATIONS_TABLE_FETCHED,
                        true
                ),
                HttpStatus.ACCEPTED
        );
    }

    @GetMapping("/summary-tiles")
    public ResponseEntity<@NonNull GenericResponse<List<SummaryTileDTO>>> getSummaryTiles(
            @AuthenticationPrincipal User principal
    ) throws EntityNotFoundException {
        return new ResponseEntity<>(
                new GenericResponse<>(
                        primaryAccountService.getSummaryTiles(principal.getUsername()),
                        "GetSummaryTilesSucceed",
                        MessageConstants.OPERATIONS_TABLE_FETCHED,
                        true
                ),
                HttpStatus.ACCEPTED
        );
    }
}
