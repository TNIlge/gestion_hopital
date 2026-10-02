package com.eilco.ing2.hopital.backend.model;

public enum StatutDemande {
    EN_ATTENTE("En attente"),
    EN_COURS("En cours"),
    VALIDEE("Validée"),
    REFUSEE("Refusée");

    private final String libelle;

    StatutDemande(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
