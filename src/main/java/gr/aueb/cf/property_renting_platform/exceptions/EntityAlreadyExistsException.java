package gr.aueb.cf.property_renting_platform.exceptions;

public class EntityAlreadyExistsException extends AppGenericException {
    private static final String DEFAULT_CODE = "AlreadyExists";

    public EntityAlreadyExistsException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
