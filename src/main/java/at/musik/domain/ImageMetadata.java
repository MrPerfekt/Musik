package at.musik.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A ImageMetadata.
 */
@Entity
@Table(name = "IMAGEMETADATA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ImageMetadata implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;



    @Column(name = "web_name")
    private String webName;

    @NotNull


    @Column(name = "file_name", nullable = false)
    private String fileName;

    @NotNull


    @Column(name = "file_path", nullable = false)
    private String filePath;

    @ManyToOne
    private User user;

    @ManyToOne
    private Story story;

    public ImageMetadata() {
    }

    public ImageMetadata(String webName, String fileName, String filePath, User user, Story story) {
        this.webName = webName;
        this.fileName = fileName;
        this.filePath = filePath;
        this.user = user;
        this.story = story;
    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ImageMetadata imageMetadata = (ImageMetadata) o;

        if ( ! Objects.equals(id, imageMetadata.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImageMetadata{" +
                "id=" + id +
                ", webName='" + webName + "'" +
                ", fileName='" + fileName + "'" +
                ", filePath='" + filePath + "'" +
                '}';
    }
}
