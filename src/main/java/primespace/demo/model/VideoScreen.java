package primespace.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "video_screen")
public class VideoScreen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tour_id")
    private Long tourId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", insertable = false, updatable = false)
    @JsonIgnore
    private Tour tour;

    private String name;

    @Column(length = 2000)
    private String youtubeUrl;

    // Position in 3D space
    private Double posX;
    private Double posY;
    private Double posZ;

    // Rotation in degrees
    private Double rotX;
    private Double rotY;
    private Double rotZ;

    // Screen dimensions (meters)
    private Double width;
    private Double height;

    // Icon type for the 3D tag (youtube, play, film, tv, live)
    @Column(name = "icon_type")
    private String iconType;

    // Max distance (meters) at which the screen is visible to visitors
    @Column(name = "visibility_range")
    private Double visibilityRange;

    public VideoScreen() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getYoutubeUrl() { return youtubeUrl; }
    public void setYoutubeUrl(String youtubeUrl) { this.youtubeUrl = youtubeUrl; }

    public Double getPosX() { return posX; }
    public void setPosX(Double posX) { this.posX = posX; }

    public Double getPosY() { return posY; }
    public void setPosY(Double posY) { this.posY = posY; }

    public Double getPosZ() { return posZ; }
    public void setPosZ(Double posZ) { this.posZ = posZ; }

    public Double getRotX() { return rotX; }
    public void setRotX(Double rotX) { this.rotX = rotX; }

    public Double getRotY() { return rotY; }
    public void setRotY(Double rotY) { this.rotY = rotY; }

    public Double getRotZ() { return rotZ; }
    public void setRotZ(Double rotZ) { this.rotZ = rotZ; }

    public Double getWidth() { return width; }
    public void setWidth(Double width) { this.width = width; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public String getIconType() { return iconType; }
    public void setIconType(String iconType) { this.iconType = iconType; }

    public Double getVisibilityRange() { return visibilityRange; }
    public void setVisibilityRange(Double visibilityRange) { this.visibilityRange = visibilityRange; }
}
