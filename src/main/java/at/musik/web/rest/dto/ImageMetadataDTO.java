package at.musik.web.rest.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the ImageMetadata entity.
 */
public class ImageMetadataDTO implements Serializable {

    private Long id;

    private String webName;

    @NotNull
    private String fileName;

    @NotNull
    private String filePath;

    private Long userId;

    private Long storyId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWebName() {
        return webName;
    }

    public void setWebName(String webName) {
        this.webName = webName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ImageMetadataDTO imageMetadataDTO = (ImageMetadataDTO) o;

        if ( ! Objects.equals(id, imageMetadataDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImageMetadataDTO{" +
                "id=" + id +
                ", webName='" + webName + "'" +
                ", fileName='" + fileName + "'" +
                ", filePath='" + filePath + "'" +
                '}';
    }
}
