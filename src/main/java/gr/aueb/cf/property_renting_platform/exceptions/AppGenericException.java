package gr.aueb.cf.property_renting_platform.exceptions;

import lombok.Getter;

@Getter
public class AppGenericException extends Exception {
    private final String code;

    public AppGenericException(String code, String message) {
        super(message);
        this.code = code;
    }
}
