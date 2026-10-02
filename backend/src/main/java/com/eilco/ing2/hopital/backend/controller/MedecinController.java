package com.eilco.ing2.hopital.backend.controller;

import com.eilco.ing2.hopital.backend.dto.ConsultationPlanningDto;
import com.eilco.ing2.hopital.backend.dto.MedecinDto;
import com.eilco.ing2.hopital.backend.dto.PointagePresenceRequestDto;
import com.eilco.ing2.hopital.backend.service.MedecinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/medecin")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@RequiredArgsConstructor
public class MedecinController {

    private final MedecinService medecinService;

    /**
     * CARE-301 : Authentification / Identification du médecin par son matricule unique.
     * Accessible par GET /api/v1/medecin/profil/{matricule}
     */
    @GetMapping("/profil/{matricule}")
    public ResponseEntity<MedecinDto> getProfil(@PathVariable("matricule") String matricule) {
        return ResponseEntity.ok(medecinService.identifierParMatricule(matricule));
    }

    /**
     * CARE-301 : Endpoint de connexion par matricule (compatible formulaire d'authentification).
     * Accessible par POST /api/v1/medecin/auth avec { "matricule": "MED-001" }
     */
    @PostMapping("/auth")
    public ResponseEntity<MedecinDto> authentifier(@RequestBody Map<String, String> body) {
        String matricule = body.get("matricule");
        return ResponseEntity.ok(medecinService.identifierParMatricule(matricule));
    }

    /**
     * CARE-302 & CARE-303 : Vue "Mon Planning" & Étanchéité stricte des données.
     * Seules les consultations validées de ce médecin sont retournées.
     * Exemple : GET /api/v1/medecin/MED-001/planning?date=2026-10-15
     */
    @GetMapping("/{matricule}/planning")
    public ResponseEntity<List<ConsultationPlanningDto>> getPlanning(
            @PathVariable("matricule") String matricule,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(medecinService.getPlanningMedecin(matricule, date));
    }

    /**
     * CARE-304 : Pointage de présence patient (Présent / Absent) & Clôture de la prise en charge.
     * Endpoint contractuel : PUT /api/v1/medecin/rendez-vous/{id}/presence
     */
    @PutMapping("/rendez-vous/{id}/presence")
    public ResponseEntity<ConsultationPlanningDto> pointerPresence(
            @PathVariable("id") Long id,
            @Valid @RequestBody PointagePresenceRequestDto requestDto
    ) {
        return ResponseEntity.ok(medecinService.pointerPresence(id, requestDto));
    }
}
