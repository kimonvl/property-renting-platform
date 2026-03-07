package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.exceptions.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    UploadResult uploadImage(MultipartFile file, String folder, String name) throws FileUploadException;

    public record UploadResult(String url, String publicId) {}
}
