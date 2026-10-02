package com.eilco.ing2.hopital.backend.controller;

import com.eilco.ing2.hopital.backend.dto.DemandeRdvRequestDto;
import com.eilco.ing2.hopital.backend.dto.DemandeRdvResponseDto;
import com.eilco.ing2.hopital.backend.service.DemandeRdvService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rendez-vous")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@RequiredArgsConstructor
public class DemandeRdvController {

    private final DemandeRdvService demandeRdvService;

    /**
     * Endpoint POST pour soumettre et enregistrer une demande de rendez-vous.
     * Statut HTTP 201 Created si la création réussit.
     */
    @PostMapping
    public ResponseEntity<DemandeRdvResponseDto> creerDemandeRdv(@Valid @RequestBody DemandeRdvRequestDto requestDto) {
        DemandeRdvResponseDto response = demandeRdvService.creerDemandeRdv(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
