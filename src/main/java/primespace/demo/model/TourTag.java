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
@Table(name = "tour_tag")
public class TourTag {

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
    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSid() { return sid; }
    public void setSid(String sid) { this.sid = sid; }
}
