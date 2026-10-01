package com.eilco.ing2.hopital.backend.service.impl;

import com.eilco.ing2.hopital.backend.dto.DemandeRdvRequestDto;
import com.eilco.ing2.hopital.backend.dto.DemandeRdvResponseDto;
import com.eilco.ing2.hopital.backend.exception.InvalidNssException;
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
        // 1. Validation métier : le N° SS doit respecter le format nom-prenom-YYYYMMDD
        validerNumeroSecuriteSociale(
                requestDto.getNom(),
                requestDto.getPrenom(),
                requestDto.getDateNaissance(),
                requestDto.getNumeroSecuriteSociale()
        );

        // 2. Génération d'un numéro de dossier unique (non prédictible)
        String numeroDossier = genererNumeroDossierUnique();

        // 3. Construction et persistance de l'entité avec statut initial "En attente"
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

        // 4. Retour sécurisé : exclusion totale du N° SS
        return DemandeRdvResponseDto.builder()
                .numeroDossier(saved.getNumeroDossier())
                .statut(saved.getStatut().getLibelle())
                .nom(saved.getNom())
                .prenom(saved.getPrenom())
                .departement(saved.getDepartement())
                .specialite(saved.getSpecialite())
                .dateSouhaitee(saved.getDateSouhaitee())
                .dateCreation(saved.getDateCreation())
                .message("Votre demande de rendez-vous a été enregistrée avec succès.")
                .build();
    }

    /**
     * Contrôle que le N° SS correspond exactement à : nom-prenom-YYYYMMDD
     * Exemple : dupont-jean-19951024
     */
    private void validerNumeroSecuriteSociale(String nom, String prenom, LocalDate dateNaissance, String nssFourni) {
        if (nssFourni == null || nssFourni.isBlank()) {
            throw new InvalidNssException("Le numéro de sécurité sociale est obligatoire.");
        }

        String nomNormalise = normaliserChaine(nom);
        String prenomNormalise = normaliserChaine(prenom);
        String dateStr = dateNaissance.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String nssAttendu = nomNormalise + "-" + prenomNormalise + "-" + dateStr;
        String nssFourniNormalise = normaliserChaine(nssFourni);

        if (!nssFourniNormalise.equalsIgnoreCase(nssAttendu)) {
            throw new InvalidNssException(
                    "Le numéro de sécurité sociale est invalide. Format attendu : " + nssAttendu
            );
        }
    }

    /**
     * Normalise une chaîne : minuscules, suppression des accents et des caractères spéciaux non désirés
     */
    private String normaliserChaine(String input) {
        if (input == null) return "";
        String decomposed = Normalizer.normalize(input.trim().toLowerCase(), Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}", "").replaceAll("[^a-z0-9-]", "");
    }

    /**
     * Génère un identifiant unique (ex : RDV-202609-A4F78B)
     */
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
