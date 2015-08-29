package at.musik.web.rest;

import at.musik.Application;
import at.musik.domain.ImageMetadata;
import at.musik.repository.ImageMetadataRepository;
import at.musik.web.rest.dto.ImageMetadataDTO;
import at.musik.web.rest.mapper.ImageMetadataMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ImageMetadataResource REST controller.
 *
 * @see ImageMetadataResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ImageMetadataResourceTest {

    private static final String DEFAULT_WEB_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_WEB_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_FILE_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_FILE_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_FILE_PATH = "SAMPLE_TEXT";
    private static final String UPDATED_FILE_PATH = "UPDATED_TEXT";

    @Inject
    private ImageMetadataRepository imageMetadataRepository;

    @Inject
    private ImageMetadataMapper imageMetadataMapper;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restImageMetadataMockMvc;

    private ImageMetadata imageMetadata;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ImageMetadataResource imageMetadataResource = new ImageMetadataResource();
        ReflectionTestUtils.setField(imageMetadataResource, "imageMetadataRepository", imageMetadataRepository);
        ReflectionTestUtils.setField(imageMetadataResource, "imageMetadataMapper", imageMetadataMapper);
        this.restImageMetadataMockMvc = MockMvcBuilders.standaloneSetup(imageMetadataResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        imageMetadata = new ImageMetadata();
        imageMetadata.setWebName(DEFAULT_WEB_NAME);
        imageMetadata.setFileName(DEFAULT_FILE_NAME);
        imageMetadata.setFilePath(DEFAULT_FILE_PATH);
    }

    @Test
    @Transactional
    public void createImageMetadata() throws Exception {
        int databaseSizeBeforeCreate = imageMetadataRepository.findAll().size();

        // Create the ImageMetadata
        ImageMetadataDTO imageMetadataDTO = imageMetadataMapper.imageMetadataToImageMetadataDTO(imageMetadata);

        restImageMetadataMockMvc.perform(post("/api/imageMetadatas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(imageMetadataDTO)))
                .andExpect(status().isCreated());

        // Validate the ImageMetadata in the database
        List<ImageMetadata> imageMetadatas = imageMetadataRepository.findAll();
        assertThat(imageMetadatas).hasSize(databaseSizeBeforeCreate + 1);
        ImageMetadata testImageMetadata = imageMetadatas.get(imageMetadatas.size() - 1);
        assertThat(testImageMetadata.getWebName()).isEqualTo(DEFAULT_WEB_NAME);
        assertThat(testImageMetadata.getFileName()).isEqualTo(DEFAULT_FILE_NAME);
        assertThat(testImageMetadata.getFilePath()).isEqualTo(DEFAULT_FILE_PATH);
    }

    @Test
    @Transactional
    public void checkFileNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = imageMetadataRepository.findAll().size();
        // set the field null
        imageMetadata.setFileName(null);

        // Create the ImageMetadata, which fails.
        ImageMetadataDTO imageMetadataDTO = imageMetadataMapper.imageMetadataToImageMetadataDTO(imageMetadata);

        restImageMetadataMockMvc.perform(post("/api/imageMetadatas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(imageMetadataDTO)))
                .andExpect(status().isBadRequest());

        List<ImageMetadata> imageMetadatas = imageMetadataRepository.findAll();
        assertThat(imageMetadatas).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFilePathIsRequired() throws Exception {
        int databaseSizeBeforeTest = imageMetadataRepository.findAll().size();
        // set the field null
        imageMetadata.setFilePath(null);

        // Create the ImageMetadata, which fails.
        ImageMetadataDTO imageMetadataDTO = imageMetadataMapper.imageMetadataToImageMetadataDTO(imageMetadata);

        restImageMetadataMockMvc.perform(post("/api/imageMetadatas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(imageMetadataDTO)))
                .andExpect(status().isBadRequest());

        List<ImageMetadata> imageMetadatas = imageMetadataRepository.findAll();
        assertThat(imageMetadatas).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllImageMetadatas() throws Exception {
        // Initialize the database
        imageMetadataRepository.saveAndFlush(imageMetadata);

        // Get all the imageMetadatas
        restImageMetadataMockMvc.perform(get("/api/imageMetadatas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(imageMetadata.getId().intValue())))
                .andExpect(jsonPath("$.[*].webName").value(hasItem(DEFAULT_WEB_NAME.toString())))
                .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME.toString())))
                .andExpect(jsonPath("$.[*].filePath").value(hasItem(DEFAULT_FILE_PATH.toString())));
    }

    @Test
    @Transactional
    public void getImageMetadata() throws Exception {
        // Initialize the database
        imageMetadataRepository.saveAndFlush(imageMetadata);

        // Get the imageMetadata
        restImageMetadataMockMvc.perform(get("/api/imageMetadatas/{id}", imageMetadata.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(imageMetadata.getId().intValue()))
            .andExpect(jsonPath("$.webName").value(DEFAULT_WEB_NAME.toString()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME.toString()))
            .andExpect(jsonPath("$.filePath").value(DEFAULT_FILE_PATH.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingImageMetadata() throws Exception {
        // Get the imageMetadata
        restImageMetadataMockMvc.perform(get("/api/imageMetadatas/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateImageMetadata() throws Exception {
        // Initialize the database
        imageMetadataRepository.saveAndFlush(imageMetadata);

		int databaseSizeBeforeUpdate = imageMetadataRepository.findAll().size();

        // Update the imageMetadata
        imageMetadata.setWebName(UPDATED_WEB_NAME);
        imageMetadata.setFileName(UPDATED_FILE_NAME);
        imageMetadata.setFilePath(UPDATED_FILE_PATH);
        
        ImageMetadataDTO imageMetadataDTO = imageMetadataMapper.imageMetadataToImageMetadataDTO(imageMetadata);

        restImageMetadataMockMvc.perform(put("/api/imageMetadatas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(imageMetadataDTO)))
                .andExpect(status().isOk());

        // Validate the ImageMetadata in the database
        List<ImageMetadata> imageMetadatas = imageMetadataRepository.findAll();
        assertThat(imageMetadatas).hasSize(databaseSizeBeforeUpdate);
        ImageMetadata testImageMetadata = imageMetadatas.get(imageMetadatas.size() - 1);
        assertThat(testImageMetadata.getWebName()).isEqualTo(UPDATED_WEB_NAME);
        assertThat(testImageMetadata.getFileName()).isEqualTo(UPDATED_FILE_NAME);
        assertThat(testImageMetadata.getFilePath()).isEqualTo(UPDATED_FILE_PATH);
    }

    @Test
    @Transactional
    public void deleteImageMetadata() throws Exception {
        // Initialize the database
        imageMetadataRepository.saveAndFlush(imageMetadata);

		int databaseSizeBeforeDelete = imageMetadataRepository.findAll().size();

        // Get the imageMetadata
        restImageMetadataMockMvc.perform(delete("/api/imageMetadatas/{id}", imageMetadata.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ImageMetadata> imageMetadatas = imageMetadataRepository.findAll();
        assertThat(imageMetadatas).hasSize(databaseSizeBeforeDelete - 1);
    }
}
