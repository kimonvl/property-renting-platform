package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;
import gr.aueb.cf.property_renting_platform.exceptions.InternalErrorException;

import java.util.UUID;

public interface StripePaymentService {
    public String createPaymentIntent(UUID bookingId, String email) throws  EntityInvalidArgumentException, EntityNotFoundException, InternalErrorException;
}
