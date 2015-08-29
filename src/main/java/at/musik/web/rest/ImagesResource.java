package at.musik.web.rest;

import at.musik.domain.ImageMetadata;
import at.musik.domain.User;
import at.musik.repository.ImageMetadataRepository;
import at.musik.service.ImageService;
import at.musik.service.UserService;
import com.codahale.metrics.annotation.Timed;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ImagesResource {
    private final Logger log = LoggerFactory.getLogger(ImagesResource.class);

    @Inject
    private ImageService imageService;
    @Inject
    private ImageMetadataRepository imageMetadataRepository;
    @Inject
    private UserService userService;

    @RequestMapping(value = "/images/upload",
        method = RequestMethod.POST)
    @Timed
    public void uploadImage(String webName, String location, String imageName, MultipartFile file) {
        User user = userService.getUserWithAuthorities();
        String filename = imageService.saveImage(file, location);
        imageMetadataRepository.save(new ImageMetadata(webName, filename, location, user));
    }

    @Timed
    @ResponseBody
    @RequestMapping(value = "/images/download", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] downloadImage(String size, String webName) throws IOException {
        ImageMetadata imageMetadata = imageMetadataRepository.findOneByWebName(webName).get();
        return imageService.loadImage(imageMetadata.getFileName(), imageMetadata.getFilePath(), ImageService.ImageSize.valueOf(size));
    }

    @Timed
    @ResponseBody
    @RequestMapping(value = "/images/getPath", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public String getImagePath(String size, String webName) throws IOException {
        ImageMetadata imageMetadata = imageMetadataRepository.findOneByWebName(webName).get();
        return ImageService.getFilePath(imageMetadata.getFilePath(), ImageService.ImageSize.valueOf(size), imageMetadata.getFileName());
    }
}
