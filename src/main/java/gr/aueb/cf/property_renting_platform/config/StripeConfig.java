package gr.aueb.cf.property_renting_platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    public StripeConfig(@Value("${stripe.secret.key}") String secretKey) {
        com.stripe.Stripe.apiKey = secretKey;
    }
}