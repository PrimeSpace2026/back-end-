package primespace.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tour_item")
public class TourItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tourId;

    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 2000)
    private String imageUrl;

    private Double price;

    private String currency;

    @Column(length = 2000)
    private String externalUrl;

    private String brand;

    private String tagSid;

    public TourItem() {
    }

    public TourItem(Long tourId, String name, String description, String imageUrl, Double price, String currency, String externalUrl, String brand, String tagSid) {
        this.tourId = tourId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.currency = currency;
        this.externalUrl = externalUrl;
        this.brand = brand;
        this.tagSid = tagSid;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getExternalUrl() { return externalUrl; }
    public void setExternalUrl(String externalUrl) { this.externalUrl = externalUrl; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getTagSid() { return tagSid; }
    public void setTagSid(String tagSid) { this.tagSid = tagSid; }
}
