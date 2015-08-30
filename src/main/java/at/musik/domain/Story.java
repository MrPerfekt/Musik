package at.musik.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A Story.
 */
@Entity
@Table(name = "STORY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Story implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 2, max = 100)    

    
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @NotNull
    @Size(min = 2, max = 500)    

    
    @Column(name = "description", length = 500, nullable = false)
    private String description;

    @NotNull
    @Size(min = 2)    

    
    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "story")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ImageMetadata> imageMetadatas = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<ImageMetadata> getImageMetadatas() {
        return imageMetadatas;
    }

    public void setImageMetadatas(Set<ImageMetadata> imageMetadatas) {
        this.imageMetadatas = imageMetadatas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Story story = (Story) o;

        if ( ! Objects.equals(id, story.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Story{" +
                "id=" + id +
                ", title='" + title + "'" +
                ", description='" + description + "'" +
                ", text='" + text + "'" +
                '}';
    }
}
