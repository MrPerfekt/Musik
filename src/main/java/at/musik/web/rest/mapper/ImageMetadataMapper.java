package at.musik.web.rest.mapper;

import at.musik.domain.*;
import at.musik.web.rest.dto.ImageMetadataDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity ImageMetadata and its DTO ImageMetadataDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ImageMetadataMapper {

    @Mapping(source = "user.id", target = "userId")
    ImageMetadataDTO imageMetadataToImageMetadataDTO(ImageMetadata imageMetadata);

    @Mapping(source = "userId", target = "user")
    ImageMetadata imageMetadataDTOToImageMetadata(ImageMetadataDTO imageMetadataDTO);

    default User userFromId(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
