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

import primespace.demo.model.TourService;
import primespace.demo.repository.TourServiceRepository;

@RestController
@RequestMapping("/api/tours/{tourId}/services")
public class TourServiceController {

    private final TourServiceRepository tourServiceRepository;

    public TourServiceController(TourServiceRepository tourServiceRepository) {
        this.tourServiceRepository = tourServiceRepository;
    }

    @GetMapping
    public List<TourService> getServices(@PathVariable Long tourId) {
        return tourServiceRepository.findByTourId(tourId);
    }

    @PostMapping
    public TourService createService(@PathVariable Long tourId, @RequestBody TourService service) {
        service.setTourId(tourId);
        return tourServiceRepository.save(service);
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<TourService> updateService(@PathVariable Long tourId, @PathVariable Long serviceId,
                                                      @RequestBody TourService details) {
        return tourServiceRepository.findById(serviceId)
                .filter(svc -> svc.getTourId().equals(tourId))
                .map(svc -> {
                    svc.setName(details.getName());
                    svc.setDescription(details.getDescription());
                    svc.setImageUrl(details.getImageUrl());
                    svc.setPhone(details.getPhone());
                    svc.setWhatsapp(details.getWhatsapp());
                    svc.setInstagram(details.getInstagram());
                    svc.setFacebook(details.getFacebook());
                    svc.setTagSid(details.getTagSid());
                    return ResponseEntity.ok(tourServiceRepository.save(svc));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> deleteService(@PathVariable Long tourId, @PathVariable Long serviceId) {
        return tourServiceRepository.findById(serviceId)
                .filter(svc -> svc.getTourId().equals(tourId))
                .map(svc -> {
                    tourServiceRepository.delete(svc);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
