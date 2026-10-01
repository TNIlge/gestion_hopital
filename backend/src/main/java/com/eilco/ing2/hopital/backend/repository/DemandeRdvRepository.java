package com.eilco.ing2.hopital.backend.repository;

import com.eilco.ing2.hopital.backend.model.DemandeRdv;
import com.eilco.ing2.hopital.backend.model.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeRdvRepository extends JpaRepository<DemandeRdv, Long>, JpaSpecificationExecutor<DemandeRdv> {

    Optional<DemandeRdv> findByNumeroDossier(String numeroDossier);

    boolean existsByNumeroDossier(String numeroDossier);

    /**
     * CARE-205 : Vérification de l'anti-conflit d'agenda médecin.
     * Recherche si le médecin sélectionné a déjà une consultation validée à la même date et heure.
     */
    boolean existsByMedecinIdAndDateConsultationAndHeureConsultationAndStatut(
            Long medecinId,
            LocalDate dateConsultation,
            LocalTime heureConsultation,
            StatutDemande statut
    );

    /**
     * CARE-205 : Variante excluant la demande courante (utile en cas de mise à jour).
     */
    boolean existsByMedecinIdAndDateConsultationAndHeureConsultationAndStatutAndIdNot(
            Long medecinId,
            LocalDate dateConsultation,
            LocalTime heureConsultation,
            StatutDemande statut,
            Long id
    );

    /**
     * CARE-302 : Planning strict médecin pour une date donnée.
     * Étanchéité absolue : seules les demandes 'Validée' ou 'Terminée' pour ce médecin sont retournées.
     */
    @Query("SELECT d FROM DemandeRdv d WHERE d.medecin.matricule = :matricule AND (d.statut = 'VALIDEE' OR d.statut = 'TERMINEE') AND d.dateConsultation = :date ORDER BY d.heureConsultation ASC")
    List<DemandeRdv> findPlanningByMedecinAndDate(
            @Param("matricule") String matricule,
            @Param("date") LocalDate date
    );

    /**
     * CARE-302 : Planning global médecin (toutes les consultations validées).
     */
    @Query("SELECT d FROM DemandeRdv d WHERE d.medecin.matricule = :matricule AND (d.statut = 'VALIDEE' OR d.statut = 'TERMINEE') ORDER BY d.dateConsultation ASC, d.heureConsultation ASC")
    List<DemandeRdv> findAllPlanningByMedecin(
            @Param("matricule") String matricule
    );
}
