package gr.aueb.cf.property_renting_platform.exceptions;

public class FileUploadException extends AppGenericException {
    private static final String DEFAULT_CODE = "FileUploadError";

    public FileUploadException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
