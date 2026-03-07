package gr.aueb.cf.property_renting_platform.DTOs.responses.auth;

import gr.aueb.cf.property_renting_platform.DTOs.responses.user.UserDTO;

public record AuthResponse(String accessToken, UserDTO user) {}
