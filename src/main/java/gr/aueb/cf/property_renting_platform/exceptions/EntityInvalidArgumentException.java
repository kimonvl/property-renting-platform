package gr.aueb.cf.property_renting_platform.exceptions;

public class EntityInvalidArgumentException extends AppGenericException {
    private static final String DEFAULT_CODE = "InvalidArgument";

    public EntityInvalidArgumentException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
