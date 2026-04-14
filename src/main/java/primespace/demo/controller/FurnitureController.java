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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import primespace.demo.model.FurnitureModel;
import primespace.demo.repository.FurnitureModelRepository;

@RestController
@RequestMapping("/api/furniture")
public class FurnitureController {

    private final FurnitureModelRepository furnitureModelRepository;

    public FurnitureController(FurnitureModelRepository furnitureModelRepository) {
        this.furnitureModelRepository = furnitureModelRepository;
    }

    @GetMapping
    public List<FurnitureModel> getAll(@RequestParam(required = false) String category) {
        if (category != null && !category.isEmpty()) {
            return furnitureModelRepository.findByCategory(category);
        }
        return furnitureModelRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FurnitureModel> getById(@PathVariable Long id) {
        return furnitureModelRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public FurnitureModel create(@RequestBody FurnitureModel model) {
        return furnitureModelRepository.save(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FurnitureModel> update(@PathVariable Long id, @RequestBody FurnitureModel details) {
        return furnitureModelRepository.findById(id)
                .map(model -> {
                    model.setName(details.getName());
                    model.setCategory(details.getCategory());
                    model.setModelUrl(details.getModelUrl());
                    model.setThumbnailUrl(details.getThumbnailUrl());
                    model.setDefaultScale(details.getDefaultScale());
                    return ResponseEntity.ok(furnitureModelRepository.save(model));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return furnitureModelRepository.findById(id)
                .map(model -> {
                    furnitureModelRepository.delete(model);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
