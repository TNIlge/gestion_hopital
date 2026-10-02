package com.eilco.ing2.hopital.backend.model;

public enum StatutPresence {
    NON_DEFINI("Non défini"),
    PRESENT("Présent"),
    ABSENT("Absent");

    private final String libelle;

    StatutPresence(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
