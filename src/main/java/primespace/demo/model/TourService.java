package primespace.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tour_service")
public class TourService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tourId;

    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 2000)
    private String imageUrl;

    private String phone;

    private String whatsapp;

    @Column(length = 500)
    private String instagram;

    @Column(length = 500)
    private String facebook;

    private String tagSid;

    @Column(length = 100)
    private String coordinates;

    public TourService() {
    }

    public TourService(Long tourId, String name, String description, String imageUrl,
                       String phone, String whatsapp, String instagram, String facebook, String tagSid) {
        this.tourId = tourId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.phone = phone;
        this.whatsapp = whatsapp;
        this.instagram = instagram;
        this.facebook = facebook;
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

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }

    public String getFacebook() { return facebook; }
    public void setFacebook(String facebook) { this.facebook = facebook; }

    public String getTagSid() { return tagSid; }
    public void setTagSid(String tagSid) { this.tagSid = tagSid; }

    public String getCoordinates() { return coordinates; }
    public void setCoordinates(String coordinates) { this.coordinates = coordinates; }
}
