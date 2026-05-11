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
@Table(name = "custom_tag")
public class CustomTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tour_id")
    private Long tourId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", insertable = false, updatable = false)
    @JsonIgnore
    private Tour tour;

    private String label;

    @Column(columnDefinition = "TEXT")
    private String description;

    // IMAGE, VIDEO, YOUTUBE, INSTAGRAM, PDF, LINK, TEXT
    @Column(name = "media_type")
    private String mediaType;

    @Column(name = "media_url", columnDefinition = "TEXT")
    private String mediaUrl;

    @Column(name = "icon_url", columnDefinition = "TEXT")
    private String iconUrl;

    @Column(name = "icon_name")
    private String iconName;

    private String color;

    @Column(name = "anchor_x")
    private Double anchorX;

    @Column(name = "anchor_y")
    private Double anchorY;

    @Column(name = "anchor_z")
    private Double anchorZ;

    @Column(name = "stem_height")
    private Double stemHeight;

    @Column(name = "stem_dir_x")
    private Double stemDirX;

    @Column(name = "stem_dir_y")
    private Double stemDirY;

    @Column(name = "stem_dir_z")
    private Double stemDirZ;

    @Column(name = "floor_index")
    private Integer floorIndex;

    private Boolean enabled;

    public CustomTag() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Double getAnchorX() { return anchorX; }
    public void setAnchorX(Double anchorX) { this.anchorX = anchorX; }

    public Double getAnchorY() { return anchorY; }
    public void setAnchorY(Double anchorY) { this.anchorY = anchorY; }

    public Double getAnchorZ() { return anchorZ; }
    public void setAnchorZ(Double anchorZ) { this.anchorZ = anchorZ; }

    public Double getStemHeight() { return stemHeight; }
    public void setStemHeight(Double stemHeight) { this.stemHeight = stemHeight; }

    public Double getStemDirX() { return stemDirX; }
    public void setStemDirX(Double stemDirX) { this.stemDirX = stemDirX; }

    public Double getStemDirY() { return stemDirY; }
    public void setStemDirY(Double stemDirY) { this.stemDirY = stemDirY; }

    public Double getStemDirZ() { return stemDirZ; }
    public void setStemDirZ(Double stemDirZ) { this.stemDirZ = stemDirZ; }

    public Integer getFloorIndex() { return floorIndex; }
    public void setFloorIndex(Integer floorIndex) { this.floorIndex = floorIndex; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
