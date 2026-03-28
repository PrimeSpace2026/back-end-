package primespace.demo.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tour_visit")
public class TourVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tour_id")
    private Long tourId;

    @Column(name = "visitor_id")
    private String visitorId;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    public TourVisit() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }
    public String getVisitorId() { return visitorId; }
    public void setVisitorId(String visitorId) { this.visitorId = visitorId; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
}
