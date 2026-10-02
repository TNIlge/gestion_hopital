package com.eilco.ing2.hopital.backend.service.impl;

import com.eilco.ing2.hopital.backend.dto.AffectationMedecinRequestDto;
import com.eilco.ing2.hopital.backend.dto.DemandeRdvAdminDto;
import com.eilco.ing2.hopital.backend.dto.MedecinDto;
import com.eilco.ing2.hopital.backend.dto.RefusDemandeRequestDto;
import com.eilco.ing2.hopital.backend.exception.AgendaConflictException;
import com.eilco.ing2.hopital.backend.exception.BusinessValidationException;
import com.eilco.ing2.hopital.backend.exception.ResourceNotFoundException;
import com.eilco.ing2.hopital.backend.model.DemandeRdv;
import com.eilco.ing2.hopital.backend.model.Medecin;
import com.eilco.ing2.hopital.backend.model.StatutDemande;
import com.eilco.ing2.hopital.backend.repository.DemandeRdvRepository;
import com.eilco.ing2.hopital.backend.repository.MedecinRepository;
import com.eilco.ing2.hopital.backend.service.SecretariatService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecretariatServiceImpl implements SecretariatService {

    private final DemandeRdvRepository demandeRdvRepository;
    private final MedecinRepository medecinRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DemandeRdvAdminDto> getDemandes(StatutDemande statut, String specialite, LocalDate date) {
        Specification<DemandeRdv> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (statut != null) {
                predicates.add(cb.equal(root.get("statut"), statut));
            }
            if (specialite != null && !specialite.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("specialite")), specialite.trim().toLowerCase()));
            }
            if (date != null) {
                // Filtre par date souhaitée ou date de consultation
                Predicate dateSouhaiteePredicate = cb.equal(root.get("dateSouhaitee"), date);
                Predicate dateConsultationPredicate = cb.equal(root.get("dateConsultation"), date);
                predicates.add(cb.or(dateSouhaiteePredicate, dateConsultationPredicate));
            }

            query.orderBy(cb.desc(root.get("dateCreation")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return demandeRdvRepository.findAll(spec)
                .stream()
                .map(this::mapToAdminDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DemandeRdvAdminDto getDemandeById(Long id) {
        DemandeRdv demande = demandeRdvRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de rendez-vous introuvable avec l'ID : " + id));
        return mapToAdminDto(demande);
    }

    @Override
    @Transactional
    public DemandeRdvAdminDto passerEnAnalysee(Long id) {
        DemandeRdv demande = demandeRdvRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de rendez-vous introuvable avec l'ID : " + id));

        demande.setStatut(StatutDemande.EN_ATTENTE);
        DemandeRdv saved = demandeRdvRepository.save(demande);
        return mapToAdminDto(saved);
    }

    @Override
    @Transactional
    public DemandeRdvAdminDto refuserDemande(Long id, RefusDemandeRequestDto requestDto) {
        DemandeRdv demande = demandeRdvRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de rendez-vous introuvable avec l'ID : " + id));

        if (requestDto == null || requestDto.getMotifRefus() == null || requestDto.getMotifRefus().trim().isEmpty()) {
            throw new BusinessValidationException("Le passage à 'Déclinée' exige un motif explicite enregistré en BDD.");
        }

        demande.setStatut(StatutDemande.DECLINEE);
        demande.setMotifRefus(requestDto.getMotifRefus().trim());
        DemandeRdv saved = demandeRdvRepository.save(demande);
        return mapToAdminDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedecinDto> getMedecinsBySpecialite(String specialite) {
        List<Medecin> medecins;
        if (specialite != null && !specialite.isBlank()) {
            medecins = medecinRepository.findBySpecialiteIgnoreCaseAndActifTrue(specialite.trim());
        } else {
            medecins = medecinRepository.findByActifTrue();
        }

        return medecins.stream().map(this::mapToMedecinDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DemandeRdvAdminDto affecterMedecin(Long demandeId, AffectationMedecinRequestDto requestDto) {
        DemandeRdv demande = demandeRdvRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de rendez-vous introuvable avec l'ID : " + demandeId));

        Medecin medecin = medecinRepository.findById(requestDto.getMedecinId())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable avec l'ID : " + requestDto.getMedecinId()));

        // CARE-204 : Vérification stricte de la spécialité
        if (!medecin.getSpecialite().equalsIgnoreCase(demande.getSpecialite())) {
            throw new BusinessValidationException(
                    "Le médecin sélectionné (" + medecin.getSpecialite() +
                    ") n'a pas la spécialité requise pour cette demande (" + demande.getSpecialite() + ")."
            );
        }

        // CARE-205 : Algorithme Anti-Conflit d'Agenda Médecin (Erreur 400 contractuelle)
        boolean aUnConflit = demandeRdvRepository.existsByMedecinIdAndDateConsultationAndHeureConsultationAndStatutAndIdNot(
                medecin.getId(),
                requestDto.getDateConsultation(),
                requestDto.getHeureConsultation(),
                StatutDemande.ACCEPTEE,
                demandeId
        );

        if (aUnConflit) {
            throw new AgendaConflictException(
                    "Conflit d'agenda : Le médecin sélectionné a déjà une consultation sur ce créneau."
            );
        }

        // CARE-206 : Passation au Statut "Acceptée" et Association Formelle
        demande.setMedecin(medecin);
        demande.setDateConsultation(requestDto.getDateConsultation());
        demande.setHeureConsultation(requestDto.getHeureConsultation());
        demande.setStatut(StatutDemande.ACCEPTEE);

        DemandeRdv saved = demandeRdvRepository.save(demande);
        return mapToAdminDto(saved);
    }

    private DemandeRdvAdminDto mapToAdminDto(DemandeRdv entity) {
        DemandeRdvAdminDto.DemandeRdvAdminDtoBuilder builder = DemandeRdvAdminDto.builder()
                .id(entity.getId())
                .numeroDossier(entity.getNumeroDossier())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .dateNaissance(entity.getDateNaissance())
                .numeroSecuriteSocialeMasque("XXXXXXXXXXXXX")
                .departement(entity.getDepartement())
                .specialite(entity.getSpecialite())
                .dateSouhaitee(entity.getDateSouhaitee())
                .motif(entity.getMotif())
                .statut(entity.getStatut())
                .statutLibelle(entity.getStatut().getLibelle())
                .motifRefus(entity.getMotifRefus())
                .dateConsultation(entity.getDateConsultation())
                .heureConsultation(entity.getHeureConsultation())
                .statutPresence(entity.getStatutPresence())
                .dateCreation(entity.getDateCreation())
                .dateMiseAJour(entity.getDateMiseAJour());

        if (entity.getMedecin() != null) {
            builder.medecinId(entity.getMedecin().getId())
                    .medecinMatricule(entity.getMedecin().getMatricule())
                    .medecinNom(entity.getMedecin().getNom())
                    .medecinPrenom(entity.getMedecin().getPrenom());
        }

        return builder.build();
    }

    private MedecinDto mapToMedecinDto(Medecin m) {
        return MedecinDto.builder()
                .id(m.getId())
                .matricule(m.getMatricule())
                .nom(m.getNom())
                .prenom(m.getPrenom())
                .specialite(m.getSpecialite())
                .departement(m.getDepartement())
                .email(m.getEmail())
                .telephone(m.getTelephone())
                .build();
    }
}
