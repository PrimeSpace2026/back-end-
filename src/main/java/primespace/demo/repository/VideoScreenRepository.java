package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.VideoScreen;

public interface VideoScreenRepository extends JpaRepository<VideoScreen, Long> {
    List<VideoScreen> findByTourId(Long tourId);
}
