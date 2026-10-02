package com.eilco.ing2.hopital.backend.service.impl;

import com.eilco.ing2.hopital.backend.dto.DemandeRdvRequestDto;
import com.eilco.ing2.hopital.backend.dto.DemandeRdvResponseDto;
import com.eilco.ing2.hopital.backend.dto.SuiviDemandeResponseDto;
import com.eilco.ing2.hopital.backend.exception.InvalidNssException;
import com.eilco.ing2.hopital.backend.exception.ResourceNotFoundException;
import com.eilco.ing2.hopital.backend.model.DemandeRdv;
import com.eilco.ing2.hopital.backend.model.StatutDemande;
import com.eilco.ing2.hopital.backend.repository.DemandeRdvRepository;
import com.eilco.ing2.hopital.backend.service.DemandeRdvService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DemandeRdvServiceImpl implements DemandeRdvService {

    private final DemandeRdvRepository demandeRdvRepository;

    @Override
    @Transactional
    public DemandeRdvResponseDto creerDemandeRdv(DemandeRdvRequestDto requestDto) {
        // 1. Validation métier CARE-102 : N° SS contrôlé à partir du Prénom et Nom
        validerNumeroSecuriteSociale(
                requestDto.getNom(),
                requestDto.getPrenom(),
                requestDto.getDateNaissance(),
                requestDto.getNumeroSecuriteSociale()
        );

        // 2. Génération d'un numéro de dossier unique (CARE-103)
        String numeroDossier = genererNumeroDossierUnique();

        // 3. Construction et persistance de l'entité avec statut initial systématiquement 'En cours' (CARE-103)
        DemandeRdv entity = DemandeRdv.builder()
                .numeroDossier(numeroDossier)
                .nom(requestDto.getNom().trim())
                .prenom(requestDto.getPrenom().trim())
                .dateNaissance(requestDto.getDateNaissance())
                .numeroSecuriteSociale(requestDto.getNumeroSecuriteSociale().trim().toLowerCase())
                .departement(requestDto.getDepartement().trim())
                .specialite(requestDto.getSpecialite().trim())
                .dateSouhaitee(requestDto.getDateSouhaitee())
                .motif(requestDto.getMotif())
                .statut(StatutDemande.EN_ATTENTE)
                .build();

        DemandeRdv saved = demandeRdvRepository.save(entity);

        // 4. Retour sécurisé CARE-104 : exclusion totale du N° SS
        return DemandeRdvResponseDto.builder()
                .numeroDossier(saved.getNumeroDossier())
                .statut(saved.getStatut().getLibelle())
                .nom(saved.getNom())
                .prenom(saved.getPrenom())
                .departement(saved.getDepartement())
                .specialite(saved.getSpecialite())
                .dateSouhaitee(saved.getDateSouhaitee())
                .dateCreation(saved.getDateCreation())
                .message("Votre demande de rendez-vous a été enregistrée avec succès. Conservez précieusement votre numéro de suivi.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SuiviDemandeResponseDto getSuiviDemande(String numeroDossier) {
        DemandeRdv demande = demandeRdvRepository.findByNumeroDossier(numeroDossier.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Aucun dossier trouvé pour le numéro de suivi : " + numeroDossier));

        SuiviDemandeResponseDto.SuiviDemandeResponseDtoBuilder builder = SuiviDemandeResponseDto.builder()
                .numeroDossier(demande.getNumeroDossier())
                .statut(demande.getStatut().getLibelle())
                .nom(demande.getNom())
                .prenom(demande.getPrenom())
                .departement(demande.getDepartement())
                .specialite(demande.getSpecialite())
                .dateSouhaitee(demande.getDateSouhaitee())
                .dateCreation(demande.getDateCreation());

        if (demande.getStatut() == StatutDemande.DECLINEE) {
            builder.motifRefus(demande.getMotifRefus());
        } else if (demande.getStatut() == StatutDemande.ACCEPTEE) {
            if (demande.getMedecin() != null) {
                builder.medecinNom("Dr. " + demande.getMedecin().getPrenom() + " " + demande.getMedecin().getNom());
                builder.medecinSpecialite(demande.getMedecin().getSpecialite());
            }
            builder.dateConsultation(demande.getDateConsultation());
            builder.heureConsultation(demande.getHeureConsultation());
        }

        return builder.build();
    }

    /**
     * CARE-102 : Contrôle que le N° SS est préfixé ou vérifié à partir du Prénom et Nom
     * Formats acceptés :
     * 1) nom-prenom-YYYYMMDD (ex: dupont-jean-19951024)
     * 2) préfixé par nom-prenom (ex: dupont-jean-1234567890123)
     */
    private void validerNumeroSecuriteSociale(String nom, String prenom, LocalDate dateNaissance, String nssFourni) {
        if (nssFourni == null || nssFourni.isBlank()) {
            throw new InvalidNssException("Le numéro de sécurité sociale est obligatoire.");
        }

        String nomNormalise = normaliserChaine(nom);
        String prenomNormalise = normaliserChaine(prenom);
        String nssFourniNormalise = normaliserChaine(nssFourni);

        String prefixeAttendu = nomNormalise + "-" + prenomNormalise;
        String prefixeSansTiret = nomNormalise + prenomNormalise;

        // Si date de naissance fournie, on accepte le format exact nom-prenom-YYYYMMDD
        boolean matchFormatDate = false;
        if (dateNaissance != null) {
            String dateStr = dateNaissance.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String nssAttendu = prefixeAttendu + "-" + dateStr;
            if (nssFourniNormalise.equalsIgnoreCase(nssAttendu)) {
                matchFormatDate = true;
            }
        }

        boolean matchPrefixe = nssFourniNormalise.startsWith(prefixeAttendu) ||
                               nssFourniNormalise.startsWith(prefixeSansTiret) ||
                               (nssFourniNormalise.contains(nomNormalise) && nssFourniNormalise.contains(prenomNormalise));

        if (!matchFormatDate && !matchPrefixe) {
            throw new InvalidNssException(
                    "Le numéro de sécurité sociale doit être vérifié à partir du Nom et du Prénom (ex: "
                            + prefixeAttendu + "-YYYYMMDD ou préfixé par " + prefixeAttendu + ")."
            );
        }
    }

    private String normaliserChaine(String input) {
        if (input == null) return "";
        String decomposed = Normalizer.normalize(input.trim().toLowerCase(), Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}", "").replaceAll("[^a-z0-9-]", "");
    }

    private String genererNumeroDossierUnique() {
        String prefix = "RDV-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-";
        String numeroDossier;
        do {
            String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
            numeroDossier = prefix + suffix;
        } while (demandeRdvRepository.existsByNumeroDossier(numeroDossier));
        return numeroDossier;
    }
}
