package primespace.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tour_event")
public class TourEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tour_id")
    private Long tourId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", insertable = false, updatable = false)
    @JsonIgnore
    private Tour tour;

    @Column(name = "visitor_id")
    private String visitorId;

    @Column(name = "event_type")
    private String eventType; // "tag_click", "product_click", "add_to_cart"

    @Column(name = "target_name")
    private String targetName; // product name or tag name

    @Column(name = "target_id")
    private String targetId; // product id or tag SID

    @Column(name = "created_at")
    private Instant createdAt;

    public TourEvent() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }
    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }
    public String getVisitorId() { return visitorId; }
    public void setVisitorId(String visitorId) { this.visitorId = visitorId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
