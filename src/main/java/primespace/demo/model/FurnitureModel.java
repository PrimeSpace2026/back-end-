package primespace.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "furniture_model")
public class FurnitureModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    @Column(length = 2000)
    private String modelUrl;

    @Column(length = 2000)
    private String thumbnailUrl;

    private Double defaultScale;

    // Per-model transform offsets (gltf models have different origins/units)
    private Double localScale;      // uniform scale for gltfLoader (e.g. 0.01 for cm-based models)
    private Double localOffsetY;    // Y offset so model sits on ground (e.g. -0.32)
    private Double localRotationY;  // default Y rotation in degrees

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getModelUrl() { return modelUrl; }
    public void setModelUrl(String modelUrl) { this.modelUrl = modelUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public Double getDefaultScale() { return defaultScale; }
    public void setDefaultScale(Double defaultScale) { this.defaultScale = defaultScale; }

    public Double getLocalScale() { return localScale; }
    public void setLocalScale(Double localScale) { this.localScale = localScale; }

    public Double getLocalOffsetY() { return localOffsetY; }
    public void setLocalOffsetY(Double localOffsetY) { this.localOffsetY = localOffsetY; }

    public Double getLocalRotationY() { return localRotationY; }
    public void setLocalRotationY(Double localRotationY) { this.localRotationY = localRotationY; }
}
