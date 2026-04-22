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
@Table(name = "tour_service")
public class TourService {

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

    @Column(length = 1000)
    private String link;

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

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

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

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getTagSid() { return tagSid; }
    public void setTagSid(String tagSid) { this.tagSid = tagSid; }

    public String getCoordinates() { return coordinates; }
    public void setCoordinates(String coordinates) { this.coordinates = coordinates; }
}
