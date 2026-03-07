package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.exceptions.InternalErrorException;
import com.stripe.model.Event;

public interface StripeWebhookService {
    public void handleEvent(Event event) throws InternalErrorException;
}
