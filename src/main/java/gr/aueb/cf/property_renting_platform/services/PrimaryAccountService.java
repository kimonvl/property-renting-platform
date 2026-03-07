package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.DTOs.responses.partner.primary_account.PropertyOperationRowDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.partner.primary_account.SummaryTileDTO;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;

import java.util.List;

public interface PrimaryAccountService {
    List<PropertyOperationRowDTO> getOperationsTable(String userEmail) throws EntityNotFoundException;
    List<SummaryTileDTO> getSummaryTiles(String userEmail) throws EntityNotFoundException;
}
