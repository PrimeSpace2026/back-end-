package primespace.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import primespace.demo.model.StagedObject;
import primespace.demo.repository.StagedObjectRepository;

@RestController
@RequestMapping("/api/tours/{tourId}/staged-objects")
public class StagedObjectController {

    private final StagedObjectRepository stagedObjectRepository;

    public StagedObjectController(StagedObjectRepository stagedObjectRepository) {
        this.stagedObjectRepository = stagedObjectRepository;
    }

    @GetMapping
    public List<StagedObject> getByTour(@PathVariable Long tourId) {
        return stagedObjectRepository.findByTourId(tourId);
    }

    @PostMapping
    public StagedObject create(@PathVariable Long tourId, @RequestBody StagedObject obj) {
        obj.setTourId(tourId);
        return stagedObjectRepository.save(obj);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StagedObject> update(@PathVariable Long tourId, @PathVariable Long id, @RequestBody StagedObject details) {
        return stagedObjectRepository.findById(id)
                .filter(obj -> obj.getTourId().equals(tourId))
                .map(obj -> {
                    obj.setFurnitureModelId(details.getFurnitureModelId());
                    obj.setModelUrl(details.getModelUrl());
                    obj.setSweepId(details.getSweepId());
                    obj.setPosX(details.getPosX());
                    obj.setPosY(details.getPosY());
                    obj.setPosZ(details.getPosZ());
                    obj.setRotX(details.getRotX());
                    obj.setRotY(details.getRotY());
                    obj.setRotZ(details.getRotZ());
                    obj.setScaleX(details.getScaleX());
                    obj.setScaleY(details.getScaleY());
                    obj.setScaleZ(details.getScaleZ());
                    obj.setLabel(details.getLabel());
                    return ResponseEntity.ok(stagedObjectRepository.save(obj));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long tourId, @PathVariable Long id) {
        return stagedObjectRepository.findById(id)
                .filter(obj -> obj.getTourId().equals(tourId))
                .map(obj -> {
                    stagedObjectRepository.delete(obj);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
