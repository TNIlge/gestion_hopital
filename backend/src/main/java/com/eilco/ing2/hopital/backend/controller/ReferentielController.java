package com.eilco.ing2.hopital.backend.controller;

import com.eilco.ing2.hopital.backend.dto.ReferentielDepartementDto;
import com.eilco.ing2.hopital.backend.service.ReferentielService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/referentiel")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@RequiredArgsConstructor
public class ReferentielController {

    private final ReferentielService referentielService;

    /**
     * CARE-001 : Exposer l'API REST de consultation des départements et spécialités.
     */
    @GetMapping("/specialites")
    public ResponseEntity<List<ReferentielDepartementDto>> getReferentielSpecialites() {
        return ResponseEntity.ok(referentielService.getReferentielSpecialites());
    }
}
