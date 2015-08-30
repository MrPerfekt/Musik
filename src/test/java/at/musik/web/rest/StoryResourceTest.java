package at.musik.web.rest;

import at.musik.Application;
import at.musik.domain.Story;
import at.musik.repository.StoryRepository;
import at.musik.web.rest.dto.StoryDTO;
import at.musik.web.rest.mapper.StoryMapper;

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
 * Test class for the StoryResource REST controller.
 *
 * @see StoryResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class StoryResourceTest {

    private static final String DEFAULT_TITLE = "SAMPLE_TEXT";
    private static final String UPDATED_TITLE = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";
    private static final String DEFAULT_TEXT = "SAMPLE_TEXT";
    private static final String UPDATED_TEXT = "UPDATED_TEXT";

    @Inject
    private StoryRepository storyRepository;

    @Inject
    private StoryMapper storyMapper;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restStoryMockMvc;

    private Story story;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StoryResource storyResource = new StoryResource();
        ReflectionTestUtils.setField(storyResource, "storyRepository", storyRepository);
        ReflectionTestUtils.setField(storyResource, "storyMapper", storyMapper);
        this.restStoryMockMvc = MockMvcBuilders.standaloneSetup(storyResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        story = new Story();
        story.setTitle(DEFAULT_TITLE);
        story.setDescription(DEFAULT_DESCRIPTION);
        story.setText(DEFAULT_TEXT);
    }

    @Test
    @Transactional
    public void createStory() throws Exception {
        int databaseSizeBeforeCreate = storyRepository.findAll().size();

        // Create the Story
        StoryDTO storyDTO = storyMapper.storyToStoryDTO(story);

        restStoryMockMvc.perform(post("/api/storys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(storyDTO)))
                .andExpect(status().isCreated());

        // Validate the Story in the database
        List<Story> storys = storyRepository.findAll();
        assertThat(storys).hasSize(databaseSizeBeforeCreate + 1);
        Story testStory = storys.get(storys.size() - 1);
        assertThat(testStory.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testStory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testStory.getText()).isEqualTo(DEFAULT_TEXT);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = storyRepository.findAll().size();
        // set the field null
        story.setTitle(null);

        // Create the Story, which fails.
        StoryDTO storyDTO = storyMapper.storyToStoryDTO(story);

        restStoryMockMvc.perform(post("/api/storys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(storyDTO)))
                .andExpect(status().isBadRequest());

        List<Story> storys = storyRepository.findAll();
        assertThat(storys).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = storyRepository.findAll().size();
        // set the field null
        story.setDescription(null);

        // Create the Story, which fails.
        StoryDTO storyDTO = storyMapper.storyToStoryDTO(story);

        restStoryMockMvc.perform(post("/api/storys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(storyDTO)))
                .andExpect(status().isBadRequest());

        List<Story> storys = storyRepository.findAll();
        assertThat(storys).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = storyRepository.findAll().size();
        // set the field null
        story.setText(null);

        // Create the Story, which fails.
        StoryDTO storyDTO = storyMapper.storyToStoryDTO(story);

        restStoryMockMvc.perform(post("/api/storys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(storyDTO)))
                .andExpect(status().isBadRequest());

        List<Story> storys = storyRepository.findAll();
        assertThat(storys).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStorys() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(story);

        // Get all the storys
        restStoryMockMvc.perform(get("/api/storys"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(story.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())));
    }

    @Test
    @Transactional
    public void getStory() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(story);

        // Get the story
        restStoryMockMvc.perform(get("/api/storys/{id}", story.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(story.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStory() throws Exception {
        // Get the story
        restStoryMockMvc.perform(get("/api/storys/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStory() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(story);

		int databaseSizeBeforeUpdate = storyRepository.findAll().size();

        // Update the story
        story.setTitle(UPDATED_TITLE);
        story.setDescription(UPDATED_DESCRIPTION);
        story.setText(UPDATED_TEXT);
        
        StoryDTO storyDTO = storyMapper.storyToStoryDTO(story);

        restStoryMockMvc.perform(put("/api/storys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(storyDTO)))
                .andExpect(status().isOk());

        // Validate the Story in the database
        List<Story> storys = storyRepository.findAll();
        assertThat(storys).hasSize(databaseSizeBeforeUpdate);
        Story testStory = storys.get(storys.size() - 1);
        assertThat(testStory.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testStory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testStory.getText()).isEqualTo(UPDATED_TEXT);
    }

    @Test
    @Transactional
    public void deleteStory() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(story);

		int databaseSizeBeforeDelete = storyRepository.findAll().size();

        // Get the story
        restStoryMockMvc.perform(delete("/api/storys/{id}", story.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Story> storys = storyRepository.findAll();
        assertThat(storys).hasSize(databaseSizeBeforeDelete - 1);
    }
}
