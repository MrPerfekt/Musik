package at.musik.repository;

import at.musik.domain.Story;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Story entity.
 */
public interface StoryRepository extends JpaRepository<Story,Long> {

    @Query("select story from Story story where story.user.login = ?#{principal.username}")
    List<Story> findAllForCurrentUser();

}
