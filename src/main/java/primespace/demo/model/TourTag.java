package primespace.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tour_tag")
public class TourTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tourId;

    private String name;

    private String sid;

    public TourTag() {}

    public TourTag(Long tourId, String name, String sid) {
        this.tourId = tourId;
        this.name = name;
        this.sid = sid;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSid() { return sid; }
    public void setSid(String sid) { this.sid = sid; }
}
