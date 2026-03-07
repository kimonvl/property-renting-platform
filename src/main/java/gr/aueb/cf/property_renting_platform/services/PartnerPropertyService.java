package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.DTOs.requests.partner.apartment.CreatePropertyRequest;
import gr.aueb.cf.property_renting_platform.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;
import gr.aueb.cf.property_renting_platform.exceptions.FileUploadException;
import gr.aueb.cf.property_renting_platform.exceptions.InternalErrorException;
import gr.aueb.cf.property_renting_platform.models.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PartnerPropertyService {
    void createProperty(CreatePropertyRequest request, List<MultipartFile> photos, Integer mainIndex, User user) throws FileUploadException, EntityInvalidArgumentException, InternalErrorException, EntityNotFoundException;
}
