package gr.aueb.cf.property_renting_platform.controllers.controller_utils;

import gr.aueb.cf.property_renting_platform.DTOs.responses.GenericResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.*;

/**
 * Factory utility responsible for creating http responses with the data
 * wrapped in {@link GenericResponse}.
 *
 * <p>This class provides to the controllers a uniform way to construct the http responses.</p>
 * */
public class ResponseFactory {
    private ResponseFactory() {
        // Prevent instantiation
    }

    /**
     * Creates a successful http response.
     *
     * @param <T> the type of the response payload.
     * @param data the data to be wrapped in {@link GenericResponse}.
     * @param message the message to be wrapped in {@link GenericResponse}.
     * @param status the status of the http {@link ResponseEntity}.
     * @return a {@link ResponseEntity} containing the {@link GenericResponse}.
     * */
    public static <T> ResponseEntity<@NonNull GenericResponse<T>> createResponse(T data, String message, HttpStatus status, boolean success) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(status);
        return builder.body(new GenericResponse<>(data, "Succeeded",message, success));
    }
}
