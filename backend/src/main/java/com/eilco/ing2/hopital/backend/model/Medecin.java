package com.eilco.ing2.hopital.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medecins")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medecin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String matricule;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 100)
    private String specialite;

    @Column(nullable = false, length = 100)
    private String departement;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String telephone;

    @Builder.Default
    @Column(nullable = false)
    private Boolean actif = true;
}
