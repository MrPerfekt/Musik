package at.musik.web.rest;

import at.musik.domain.ImageMetadata;
import at.musik.domain.User;
import at.musik.repository.ImageMetadataRepository;
import at.musik.service.ImageService;
import at.musik.service.UserService;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.annotation.MultipartConfig;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Optional;

@RestController
@RequestMapping("/api")
//@MultipartConfig(maxFileSize = 15 * 1000 * 1000)
public class ImagesResource {
    private final Logger log = LoggerFactory.getLogger(ImagesResource.class);

    @Inject
    private ImageService imageService;
    @Inject
    private ImageMetadataRepository imageMetadataRepository;
    @Inject
    private UserService userService;

    @Timed
    @RequestMapping(value = "/images/upload",
        method = RequestMethod.POST) //The field file exceeds its maximum permitted size of 1048576 bytes.
    public void uploadImage(@RequestParam(required = true) String webName, @RequestParam(defaultValue = "") String location, @RequestBody MultipartFile file) {
        String filename = imageService.saveImage(file, webName, location);
        User user = userService.getUserWithAuthorities();
        Optional<ImageMetadata> optionalMetadata = imageMetadataRepository.findOneByWebName(webName);
        if (optionalMetadata.isPresent()) {
            ImageMetadata imageMetadata = optionalMetadata.get();
            imageMetadata.setFileName(filename);
            imageMetadata.setFilePath(location);
            imageMetadata.setUser(user);
            imageMetadataRepository.save(imageMetadata);
        } else {
            imageMetadataRepository.save(new ImageMetadata(webName, filename, location, user));
        }
    }

    @Timed
    @ResponseBody
    @RequestMapping(value = "/images/download", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] downloadImage(@RequestParam(defaultValue = "thumbnail") String size, @RequestParam(required = true) String webName) {
        ImageMetadata imageMetadata = imageMetadataRepository.findOneByWebName(webName).get();
        try {
            return imageService.loadImage(imageMetadata.getFileName(), imageMetadata.getFilePath(), ImageService.ImageSize.valueOf(size));
        } catch (IOException e) {
            log.error("Failed to read image", e);
            return null;
        }
    }

    @Timed
    @ResponseBody
    @RequestMapping(value = "/images/getPath", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String getImagePath(@RequestParam(defaultValue = "thumbnail") String size, @RequestParam(required = true) String webName) {
        Optional<ImageMetadata> optionalMetadata = imageMetadataRepository.findOneByWebName(webName);
        if (!optionalMetadata.isPresent()) {
            return "";
        }
        ImageMetadata imageMetadata = optionalMetadata.get();
        return ImageService.getFilePath(imageMetadata.getFilePath(), ImageService.ImageSize.valueOf(size), imageMetadata.getFileName());
    }
}
