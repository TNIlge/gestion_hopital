package com.eilco.ing2.hopital.backend.exception;

public class BusinessValidationException extends RuntimeException {
    public BusinessValidationException(String message) {
        super(message);
    }
}
