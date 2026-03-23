package primespace.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import primespace.demo.model.Tour;
import primespace.demo.repository.TourRepository;

@RestController
@RequestMapping("/api/seed")
public class SeedController {

    private final TourRepository tourRepository;

    public SeedController(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    @PostMapping
    public ResponseEntity<List<Tour>> seed() {
        if (tourRepository.count() > 0) {
            return ResponseEntity.ok(tourRepository.findAll());
        }

        List<Tour> tours = List.of(
            new Tour("Clayton Hotel Belfast",
                "Visite virtuelle compl\u00e8te de l'h\u00f4tel 5 \u00e9toiles avec chambres, lobby et espaces communs.",
                "H\u00f4tellerie", "https://my.matterport.com/api/v2/player/models/1aWQXDdxWnG/thumb", 2500.0,
                "https://my.matterport.com/show/?m=1aWQXDdxWnG"),
            new Tour("Villa ireland",
                "Villa de luxe. Visite 3D avec plans interactifs.",
                "Immobilier", "https://my.matterport.com/api/v2/player/models/t84zwhnXjvJ/thumb", 280.0,
                "https://my.matterport.com/show/?m=t84zwhnXjvJ"),
            new Tour("Boutique Mode Avenue",
                "Showroom de mode haut de gamme captur\u00e9 pour exp\u00e9rience shopping virtuel.",
                "Commerce", "https://my.matterport.com/api/v2/player/models/i4XHNhtSSYx/thumb", 150.0,
                "https://my.matterport.com/show?play=1&lang=en-US&m=i4XHNhtSSYx"),
            new Tour("The Ivory Pavillon",
                "Exposition permanente digitalis\u00e9e pour visites \u00e0 distance et archives num\u00e9riques.",
                "Wedding venue", "https://my.matterport.com/api/v2/player/models/nwzR6S7LzMD/thumb", 800.0,
                "https://my.matterport.com/show?play=1&lang=en-US&m=nwzR6S7LzMD"),
            new Tour("Oro Restaurant O2 Barbados",
                "Capture de l'ambiance unique du restaurant pour pr\u00e9visualisation et \u00e9v\u00e9nements.",
                "Restaurant", "https://my.matterport.com/api/v2/player/models/hUiuMVtqB7F/thumb", 320.0,
                "https://my.matterport.com/show?play=1&lang=en-US&m=hUiuMVtqB7F"),
            new Tour("Si\u00e8ge Social TechCorp",
                "Bureaux modernes captur\u00e9s pour recrutement virtuel et visite clients.",
                "Entreprise", "https://my.matterport.com/api/v2/player/models/3kVVQfg1wSy/thumb", 1200.0,
                "https://my.matterport.com/show?play=1&lang=en-US&m=3kVVQfg1wSy")
        );

        return ResponseEntity.ok(tourRepository.saveAll(tours));
    }
}
