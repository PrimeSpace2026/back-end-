package primespace.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import primespace.demo.model.Tour;

public interface TourRepository extends JpaRepository<Tour, Long> {
}
