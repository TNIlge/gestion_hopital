package com.eilco.ing2.hopital.backend.service.impl;

import com.eilco.ing2.hopital.backend.dto.ConsultationPlanningDto;
import com.eilco.ing2.hopital.backend.dto.MedecinDto;
import com.eilco.ing2.hopital.backend.dto.PointagePresenceRequestDto;
import com.eilco.ing2.hopital.backend.exception.BusinessValidationException;
import com.eilco.ing2.hopital.backend.exception.ResourceNotFoundException;
import com.eilco.ing2.hopital.backend.model.DemandeRdv;
import com.eilco.ing2.hopital.backend.model.Medecin;
import com.eilco.ing2.hopital.backend.model.StatutDemande;
import com.eilco.ing2.hopital.backend.repository.DemandeRdvRepository;
import com.eilco.ing2.hopital.backend.repository.MedecinRepository;
import com.eilco.ing2.hopital.backend.service.MedecinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedecinServiceImpl implements MedecinService {

    private final MedecinRepository medecinRepository;
    private final DemandeRdvRepository demandeRdvRepository;

    @Override
    @Transactional(readOnly = true)
    public MedecinDto identifierParMatricule(String matricule) {
        if (matricule == null || matricule.trim().isEmpty()) {
            throw new BusinessValidationException("Le matricule unique id_medecin est obligatoire dans la requête d'accès.");
        }

        Medecin medecin = medecinRepository.findByMatricule(matricule.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Aucun médecin trouvé avec le matricule : " + matricule));

        return MedecinDto.builder()
                .id(medecin.getId())
                .matricule(medecin.getMatricule())
                .nom(medecin.getNom())
                .prenom(medecin.getPrenom())
                .specialite(medecin.getSpecialite())
                .departement(medecin.getDepartement())
                .email(medecin.getEmail())
                .telephone(medecin.getTelephone())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultationPlanningDto> getPlanningMedecin(String matricule, LocalDate date) {
        if (matricule == null || matricule.trim().isEmpty()) {
            throw new BusinessValidationException("Le matricule unique id_medecin est obligatoire pour consulter le planning.");
        }

        String mat = matricule.trim().toUpperCase();
        if (!medecinRepository.existsByMatricule(mat)) {
            throw new ResourceNotFoundException("Médecin introuvable pour le matricule : " + matricule);
        }

        List<DemandeRdv> consultations;
        if (date != null) {
            consultations = demandeRdvRepository.findPlanningByMedecinAndDate(mat, date);
        } else {
            consultations = demandeRdvRepository.findAllPlanningByMedecin(mat);
        }

        return consultations.stream()
                .map(this::mapToPlanningDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConsultationPlanningDto pointerPresence(Long rendezVousId, PointagePresenceRequestDto requestDto) {
        DemandeRdv rdv = demandeRdvRepository.findById(rendezVousId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation introuvable avec l'ID : " + rendezVousId));

        if (rdv.getStatut() != StatutDemande.VALIDEE && rdv.getStatut() != StatutDemande.TERMINEE) {
            throw new BusinessValidationException("Seul un rendez-vous validé peut faire l'objet d'un pointage de présence.");
        }

        if (requestDto == null || requestDto.getPresence() == null) {
            throw new BusinessValidationException("Le statut de présence est obligatoire (PRESENT ou ABSENT).");
        }

        // CARE-304 : Mise à jour du statut de présence et clôture
        rdv.setStatutPresence(requestDto.getPresence());
        rdv.setDatePointage(LocalDateTime.now());
        rdv.setStatut(StatutDemande.TERMINEE); // Clôture et archivage de la prise en charge

        DemandeRdv saved = demandeRdvRepository.save(rdv);
        return mapToPlanningDto(saved);
    }

    private ConsultationPlanningDto mapToPlanningDto(DemandeRdv d) {
        return ConsultationPlanningDto.builder()
                .id(d.getId())
                .numeroDossier(d.getNumeroDossier())
                .nomPatient(d.getNom())
                .prenomPatient(d.getPrenom())
                .dateNaissancePatient(d.getDateNaissance())
                .motif(d.getMotif())
                .dateConsultation(d.getDateConsultation())
                .heureConsultation(d.getHeureConsultation())
                .specialite(d.getSpecialite())
                .statut(d.getStatut().getLibelle())
                .statutPresence(d.getStatutPresence())
                .datePointage(d.getDatePointage())
                .build();
    }
}
