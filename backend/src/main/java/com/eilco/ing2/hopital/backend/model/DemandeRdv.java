package com.eilco.ing2.hopital.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "demandes_rdv")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeRdv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_dossier", unique = true, nullable = false, length = 50)
    private String numeroDossier;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Column(name = "numero_securite_sociale", nullable = false, length = 100)
    private String numeroSecuriteSociale;

    @Column(nullable = false, length = 100)
    private String departement;

    @Column(nullable = false, length = 100)
    private String specialite;

    @Column(name = "date_souhaitee", nullable = false)
    private LocalDate dateSouhaitee;

    @Column(columnDefinition = "TEXT")
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutDemande statut;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_mise_a_jour")
    private LocalDateTime dateMiseAJour;

    @PrePersist
    public void prePersist() {
        if (this.statut == null) {
            this.statut = StatutDemande.EN_ATTENTE;
        }
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
        this.dateMiseAJour = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.dateMiseAJour = LocalDateTime.now();
    }
}
