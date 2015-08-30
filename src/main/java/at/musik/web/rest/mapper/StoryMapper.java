package at.musik.web.rest.mapper;

import at.musik.domain.*;
import at.musik.web.rest.dto.StoryDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Story and its DTO StoryDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface StoryMapper {

    @Mapping(source = "user.id", target = "userId")
    StoryDTO storyToStoryDTO(Story story);

    @Mapping(source = "userId", target = "user")
    @Mapping(target = "imageMetadatas", ignore = true)
    Story storyDTOToStory(StoryDTO storyDTO);

    default User userFromId(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
