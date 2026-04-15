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
@Table(name = "staged_object")
public class StagedObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tour_id")
    private Long tourId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", insertable = false, updatable = false)
    @JsonIgnore
    private Tour tour;

    @Column(name = "furniture_model_id")
    private Long furnitureModelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "furniture_model_id", insertable = false, updatable = false)
    @JsonIgnore
    private FurnitureModel furnitureModel;

    @Column(length = 2000)
    private String modelUrl;

    @Column(name = "sweep_id")
    private String sweepId;

    @Column(name = "pos_x")
    private Double posX;
    @Column(name = "pos_y")
    private Double posY;
    @Column(name = "pos_z")
    private Double posZ;

    @Column(name = "rot_x")
    private Double rotX;
    @Column(name = "rot_y")
    private Double rotY;
    @Column(name = "rot_z")
    private Double rotZ;

    @Column(name = "scale_x")
    private Double scaleX;
    @Column(name = "scale_y")
    private Double scaleY;
    @Column(name = "scale_z")
    private Double scaleZ;

    private String label;

    @Column(name = "local_scale")
    private Double localScale;

    @Column(name = "local_offset_y")
    private Double localOffsetY;

    @Column(name = "local_rotation_y")
    private Double localRotationY;

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

    public Long getFurnitureModelId() { return furnitureModelId; }
    public void setFurnitureModelId(Long furnitureModelId) { this.furnitureModelId = furnitureModelId; }

    public FurnitureModel getFurnitureModel() { return furnitureModel; }
    public void setFurnitureModel(FurnitureModel furnitureModel) { this.furnitureModel = furnitureModel; }

    public String getModelUrl() { return modelUrl; }
    public void setModelUrl(String modelUrl) { this.modelUrl = modelUrl; }

    public String getSweepId() { return sweepId; }
    public void setSweepId(String sweepId) { this.sweepId = sweepId; }

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

    public Double getScaleX() { return scaleX; }
    public void setScaleX(Double scaleX) { this.scaleX = scaleX; }

    public Double getScaleY() { return scaleY; }
    public void setScaleY(Double scaleY) { this.scaleY = scaleY; }

    public Double getScaleZ() { return scaleZ; }
    public void setScaleZ(Double scaleZ) { this.scaleZ = scaleZ; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Double getLocalScale() { return localScale; }
    public void setLocalScale(Double localScale) { this.localScale = localScale; }

    public Double getLocalOffsetY() { return localOffsetY; }
    public void setLocalOffsetY(Double localOffsetY) { this.localOffsetY = localOffsetY; }

    public Double getLocalRotationY() { return localRotationY; }
    public void setLocalRotationY(Double localRotationY) { this.localRotationY = localRotationY; }
}
