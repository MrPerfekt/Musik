package at.musik.web.rest;

//import at.musik.domain.User;
//import at.musik.repository.UserRepository;
//import at.musik.service.ImageService;
//import com.codahale.metrics.annotation.Timed;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.inject.Inject;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.List;
//
///**
// * REST controller for managing users.
// */
//@RestController
//@RequestMapping("/api")
//public class ImagesResource {
//
//    private final Logger log = LoggerFactory.getLogger(ImagesResource.class);
//
//    @Inject
//    private ImageService imageService;
//
//    /**
//     * GET  /users/:login -> get the "login" user.
//     */
//    @RequestMapping(value = "/images/upload",
//        method = RequestMethod.POST,
//        produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed
////    ResponseEntity<User> uploadImage(@PathVariable String login) {
//    void uploadImage(@RequestParam MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
//         file.getBytes();
//        imageService.saveImage(file);
//    }
//}

import at.musik.service.ImageService;
import com.codahale.metrics.annotation.Timed;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class ImagesResource {

    @Inject
    private ImageService imageService;

    /**
     * Accept an image upload via POST and notify a Reactor that the image needs to be thumbnailed. Asynchronously respond
     * to the client when the thumbnailing has completed.
     *
     * @param channel
     *     the channel on which to send an HTTP response
     * @param thumbnail
     *     a reference to the shared thumbnail path
     * @param reactor
     *     the Reactor on which to publish events
     *
     * @return a consumer to handle HTTP requests
     */
    /**
     * GET  /users/:login -> get the "login" user.
     */
    @RequestMapping(value = "/images/upload",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public String uploadImage(String location, MultipartFile file) {
        return imageService.saveImage(file, location);
    }
}
