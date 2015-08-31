package at.musik.web.rest;

import com.codahale.metrics.annotation.Timed;
import at.musik.domain.Story;
import at.musik.repository.StoryRepository;
import at.musik.web.rest.util.HeaderUtil;
import at.musik.web.rest.util.PaginationUtil;
import at.musik.web.rest.dto.StoryDTO;
import at.musik.web.rest.mapper.StoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing Story.
 */
@RestController
@RequestMapping("/api")
public class StoryResource {

    private final Logger log = LoggerFactory.getLogger(StoryResource.class);

    @Inject
    private StoryRepository storyRepository;

    @Inject
    private StoryMapper storyMapper;


    /**
     * POST  /storys -> Create a new story.
     */
    @RequestMapping(value = "/storys",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StoryDTO> create(@Valid @RequestBody StoryDTO storyDTO) throws URISyntaxException {
        log.debug("REST request to save Story : {}", storyDTO);
        if (storyDTO.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new story cannot already have an ID").body(null);
        }
        Story story = storyMapper.storyDTOToStory(storyDTO);
        Story result = storyRepository.save(story);
        return ResponseEntity.created(new URI("/api/storys/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("story", result.getId().toString()))
                .body(storyMapper.storyToStoryDTO(result));
    }

    /**
     * PUT  /storys -> Updates an existing story.
     */
    @RequestMapping(value = "/storys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StoryDTO> update(@Valid @RequestBody StoryDTO storyDTO) throws URISyntaxException {
        log.debug("REST request to update Story : {}", storyDTO);
        if (storyDTO.getId() == null) {
            return create(storyDTO);
        }
        Story story = storyMapper.storyDTOToStory(storyDTO);
        Story result = storyRepository.save(story);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("story", storyDTO.getId().toString()))
                .body(storyMapper.storyToStoryDTO(result));
    }

    /**
     * GET  /storys -> get all the storys.
     */
    @RequestMapping(value = "/storys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<StoryDTO>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Story> page = storyRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/storys", offset, limit);
        return new ResponseEntity<>(page.getContent().stream()
            .map(storyMapper::storyToStoryDTO)
            .collect(Collectors.toCollection(LinkedList::new)), headers, HttpStatus.OK);
    }

    /**
     * GET  /storys/:id -> get the "id" story.
     */
    @RequestMapping(value = "/storys/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StoryDTO> get(@PathVariable Long id) {
        log.debug("REST request to get Story : {}", id);
        return Optional.ofNullable(storyRepository.findOne(id))
            .map(storyMapper::storyToStoryDTO)
            .map(storyDTO -> new ResponseEntity<>(
                storyDTO,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /storys/:id -> delete the "id" story.
     */
    @RequestMapping(value = "/storys/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Story : {}", id);
        storyRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("story", id.toString())).build();
    }
}
