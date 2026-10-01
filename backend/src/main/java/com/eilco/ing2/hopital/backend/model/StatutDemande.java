package com.eilco.ing2.hopital.backend.model;

public enum StatutDemande {
    EN_COURS("En cours"),
    ANALYSEE("Analysée"),
    VALIDEE("Validée"),
    REFUSEE("Refusée"),
    TERMINEE("Terminée");

    private final String libelle;

    StatutDemande(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
