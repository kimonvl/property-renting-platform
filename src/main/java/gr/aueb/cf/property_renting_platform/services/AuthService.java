package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.DTOs.requests.auth.LoginRequest;
import gr.aueb.cf.property_renting_platform.DTOs.requests.auth.RegisterRequest;
import gr.aueb.cf.property_renting_platform.DTOs.responses.user.UserDTO;
import gr.aueb.cf.property_renting_platform.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.property_renting_platform.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;
import gr.aueb.cf.property_renting_platform.exceptions.InternalErrorException;

public interface AuthService {
    UserDTO register(RegisterRequest req) throws EntityAlreadyExistsException, EntityInvalidArgumentException;
    AuthServiceImpl.AuthResult login(LoginRequest request) throws EntityInvalidArgumentException, InternalErrorException, EntityNotFoundException;
    AuthServiceImpl.AuthResult refresh(String refreshTokenValue)
            throws EntityNotFoundException, EntityInvalidArgumentException;
    void logout(String refreshTokenValue) throws EntityNotFoundException;
}
