package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.StagedObject;

public interface StagedObjectRepository extends JpaRepository<StagedObject, Long> {
    List<StagedObject> findByTourId(Long tourId);
        @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM StagedObject e WHERE e.tourId = :tourId")
    void deleteAllByTourId(@org.springframework.data.repository.query.Param("tourId") Long tourId);
}
