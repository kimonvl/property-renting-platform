package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.exceptions.FileUploadException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService{

    private final Cloudinary cloudinary;

    @Override
    public UploadResult uploadImage(MultipartFile file, String folder, String name) throws FileUploadException {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "public_id", name,
                            "resource_type", "image"
                    )
            );

            String secureUrl = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            log.info("Image uploaded successfully with publicId={}", publicId);
            return new UploadResult(secureUrl, publicId);
        } catch (IOException e) {
            log.error("Image upload failed for folder={} and file name={}", folder, name);
            throw new FileUploadException("Cloudinary", "Cloudinary upload failed for: " + file.getOriginalFilename());
        }
    }
}
