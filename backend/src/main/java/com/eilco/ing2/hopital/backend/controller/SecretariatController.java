package com.eilco.ing2.hopital.backend.controller;

import com.eilco.ing2.hopital.backend.dto.AffectationMedecinRequestDto;
import com.eilco.ing2.hopital.backend.dto.DemandeRdvAdminDto;
import com.eilco.ing2.hopital.backend.dto.MedecinDto;
import com.eilco.ing2.hopital.backend.dto.RefusDemandeRequestDto;
import com.eilco.ing2.hopital.backend.model.StatutDemande;
import com.eilco.ing2.hopital.backend.service.SecretariatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/secretariat")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@RequiredArgsConstructor
public class SecretariatController {

    private final SecretariatService secretariatService;

    /**
     * CARE-201 & CARE-202 : Tableau de bord secrétariat avec vue globale et filtres multi-critères.
     * Exemple : GET /api/v1/secretariat/demandes?statut=EN_ATTENTE&specialite=Cardiologie+interventionnelle
     */
    @GetMapping("/demandes")
    public ResponseEntity<List<DemandeRdvAdminDto>> getDemandes(
            @RequestParam(value = "statut", required = false) StatutDemande statut,
            @RequestParam(value = "specialite", required = false) String specialite,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(secretariatService.getDemandes(statut, specialite, date));
    }

    /**
     * Consultation détaillée d'une demande.
     */
    @GetMapping("/demandes/{id}")
    public ResponseEntity<DemandeRdvAdminDto> getDemandeById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(secretariatService.getDemandeById(id));
    }

    /**
     * CARE-203 : Passage d'une demande au statut 'Analysée'.
     */
    @PatchMapping("/demandes/{id}/analyser")
    public ResponseEntity<DemandeRdvAdminDto> passerEnAnalysee(@PathVariable("id") Long id) {
        return ResponseEntity.ok(secretariatService.passerEnAnalysee(id));
    }

    /**
     * CARE-203 : Refus d'une demande avec saisie obligatoire d'un motif.
     */
    @PatchMapping("/demandes/{id}/refuser")
    public ResponseEntity<DemandeRdvAdminDto> refuserDemande(
            @PathVariable("id") Long id,
            @Valid @RequestBody RefusDemandeRequestDto requestDto
    ) {
        return ResponseEntity.ok(secretariatService.refuserDemande(id, requestDto));
    }

    /**
     * CARE-204 : Liste des médecins filtrés par spécialité pour la modale d'affectation.
     */
    @GetMapping("/medecins")
    public ResponseEntity<List<MedecinDto>> getMedecins(
            @RequestParam(value = "specialite", required = false) String specialite
    ) {
        return ResponseEntity.ok(secretariatService.getMedecinsBySpecialite(specialite));
    }

    /**
     * CARE-205 & CARE-206 :
     * Affectation du médecin, programmation horaire avec algorithme anti-conflit d'agenda,
     * et passation au statut 'Validée'.
     */
    @PostMapping("/demandes/{id}/affecter")
    public ResponseEntity<DemandeRdvAdminDto> affecterMedecin(
            @PathVariable("id") Long id,
            @Valid @RequestBody AffectationMedecinRequestDto requestDto
    ) {
        return ResponseEntity.ok(secretariatService.affecterMedecin(id, requestDto));
    }
}
