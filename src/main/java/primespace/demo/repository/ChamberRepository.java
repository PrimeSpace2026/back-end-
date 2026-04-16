package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.Chamber;

public interface ChamberRepository extends JpaRepository<Chamber, Long> {
    List<Chamber> findByTourId(Long tourId);
}
