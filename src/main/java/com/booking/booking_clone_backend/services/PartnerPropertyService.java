package com.booking.booking_clone_backend.services;

import com.booking.booking_clone_backend.DTOs.requests.partner.apartment.CreatePropertyRequest;
import com.booking.booking_clone_backend.exceptions.EntityInvalidArgumentException;
import com.booking.booking_clone_backend.exceptions.EntityNotFoundException;
import com.booking.booking_clone_backend.exceptions.FileUploadException;
import com.booking.booking_clone_backend.exceptions.InternalErrorException;
import com.booking.booking_clone_backend.models.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PartnerPropertyService {
    void createProperty(CreatePropertyRequest request, List<MultipartFile> photos, Integer mainIndex, User user) throws FileUploadException, EntityInvalidArgumentException, InternalErrorException, EntityNotFoundException;
}
