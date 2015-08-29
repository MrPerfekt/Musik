package at.musik.web.rest;

import com.codahale.metrics.annotation.Timed;
import at.musik.domain.ImageMetadata;
import at.musik.repository.ImageMetadataRepository;
import at.musik.web.rest.util.HeaderUtil;
import at.musik.web.rest.util.PaginationUtil;
import at.musik.web.rest.dto.ImageMetadataDTO;
import at.musik.web.rest.mapper.ImageMetadataMapper;
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
 * REST controller for managing ImageMetadata.
 */
@RestController
@RequestMapping("/api")
public class ImageMetadataResource {

    private final Logger log = LoggerFactory.getLogger(ImageMetadataResource.class);

    @Inject
    private ImageMetadataRepository imageMetadataRepository;

    @Inject
    private ImageMetadataMapper imageMetadataMapper;

    /**
     * POST  /imageMetadatas -> Create a new imageMetadata.
     */
    @RequestMapping(value = "/imageMetadatas",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ImageMetadataDTO> create(@Valid @RequestBody ImageMetadataDTO imageMetadataDTO) throws URISyntaxException {
        log.debug("REST request to save ImageMetadata : {}", imageMetadataDTO);
        if (imageMetadataDTO.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new imageMetadata cannot already have an ID").body(null);
        }
        ImageMetadata imageMetadata = imageMetadataMapper.imageMetadataDTOToImageMetadata(imageMetadataDTO);
        ImageMetadata result = imageMetadataRepository.save(imageMetadata);
        return ResponseEntity.created(new URI("/api/imageMetadatas/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("imageMetadata", result.getId().toString()))
                .body(imageMetadataMapper.imageMetadataToImageMetadataDTO(result));
    }

    /**
     * PUT  /imageMetadatas -> Updates an existing imageMetadata.
     */
    @RequestMapping(value = "/imageMetadatas",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ImageMetadataDTO> update(@Valid @RequestBody ImageMetadataDTO imageMetadataDTO) throws URISyntaxException {
        log.debug("REST request to update ImageMetadata : {}", imageMetadataDTO);
        if (imageMetadataDTO.getId() == null) {
            return create(imageMetadataDTO);
        }
        ImageMetadata imageMetadata = imageMetadataMapper.imageMetadataDTOToImageMetadata(imageMetadataDTO);
        ImageMetadata result = imageMetadataRepository.save(imageMetadata);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("imageMetadata", imageMetadataDTO.getId().toString()))
                .body(imageMetadataMapper.imageMetadataToImageMetadataDTO(result));
    }

    /**
     * GET  /imageMetadatas -> get all the imageMetadatas.
     */
    @RequestMapping(value = "/imageMetadatas",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<ImageMetadataDTO>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<ImageMetadata> page = imageMetadataRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/imageMetadatas", offset, limit);
        return new ResponseEntity<>(page.getContent().stream()
            .map(imageMetadataMapper::imageMetadataToImageMetadataDTO)
            .collect(Collectors.toCollection(LinkedList::new)), headers, HttpStatus.OK);
    }

    /**
     * GET  /imageMetadatas/:id -> get the "id" imageMetadata.
     */
    @RequestMapping(value = "/imageMetadatas/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ImageMetadataDTO> get(@PathVariable Long id) {
        log.debug("REST request to get ImageMetadata : {}", id);
        return Optional.ofNullable(imageMetadataRepository.findOne(id))
            .map(imageMetadataMapper::imageMetadataToImageMetadataDTO)
            .map(imageMetadataDTO -> new ResponseEntity<>(
                imageMetadataDTO,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /imageMetadatas/:id -> delete the "id" imageMetadata.
     */
    @RequestMapping(value = "/imageMetadatas/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete ImageMetadata : {}", id);
        imageMetadataRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("imageMetadata", id.toString())).build();
    }
}
