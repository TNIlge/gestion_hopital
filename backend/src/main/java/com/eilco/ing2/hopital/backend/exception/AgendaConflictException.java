package com.eilco.ing2.hopital.backend.exception;

/**
 * CARE-205 : Exception levée lors d'un conflit d'agenda médecin.
 * Déclenche une réponse HTTP 400 Bad Request avec le message contractuel.
 */
public class AgendaConflictException extends RuntimeException {
    public AgendaConflictException(String message) {
        super(message);
    }
}
