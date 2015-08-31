package at.musik.repository;

import at.musik.domain.ImageMetadata;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the ImageMetadata entity.
 */
public interface ImageMetadataRepository extends JpaRepository<ImageMetadata,Long> {

    @Query("select imageMetadata from ImageMetadata imageMetadata where imageMetadata.user.login = ?#{principal.username}")
    List<ImageMetadata> findAllForCurrentUser();

    Optional<ImageMetadata> findOneByWebName(String webName);

    @Query(value = "select imageMetadata from IMAGEMETADATA imageMetadata where imageMetadata.story_id = ?1", nativeQuery = true)
    List<ImageMetadata> findAllByStory(Long storyId);

    @Query(value = "select imageMetadata.web_name from IMAGEMETADATA imageMetadata where imageMetadata.story_id = ?1", nativeQuery = true)
    List<String> findAllWebNamesByStory(Long storyId);
}
