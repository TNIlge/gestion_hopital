package com.eilco.ing2.hopital.backend.controller;

import com.eilco.ing2.hopital.backend.dto.DemandeRdvRequestDto;
import com.eilco.ing2.hopital.backend.dto.DemandeRdvResponseDto;
import com.eilco.ing2.hopital.backend.service.DemandeRdvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DemandeRdvControllerTest {

    @Mock
    private DemandeRdvService demandeRdvService;

    @InjectMocks
    private DemandeRdvController demandeRdvController;

    private DemandeRdvRequestDto validRequest;

    @BeforeEach
    void setUp() {
        validRequest = DemandeRdvRequestDto.builder()
                .nom("Dupont")
                .prenom("Jean")
                .dateNaissance(LocalDate.of(1995, 10, 24))
                .numeroSecuriteSociale("dupont-jean-19951024")
                .departement("Services Cardiologiques")
                .specialite("Rythmologie cardiaque")
                .dateSouhaitee(LocalDate.of(2026, 11, 15))
                .motif("Consultation de contrôle")
                .build();
    }

    @Test
    @DisplayName("Le contrôleur doit retourner 201 Created avec les informations de la demande")
    void shouldReturn201WhenDemandeIsCreated() {
        DemandeRdvResponseDto mockResponse = DemandeRdvResponseDto.builder()
                .numeroDossier("RDV-202609-XYZ123")
                .statut("En attente")
                .nom("Dupont")
                .prenom("Jean")
                .departement("Services Cardiologiques")
                .specialite("Rythmologie cardiaque")
                .dateSouhaitee(LocalDate.of(2026, 11, 15))
                .dateCreation(LocalDateTime.now())
                .message("Votre demande de rendez-vous a été enregistrée avec succès.")
                .build();

        when(demandeRdvService.creerDemandeRdv(any(DemandeRdvRequestDto.class))).thenReturn(mockResponse);

        ResponseEntity<DemandeRdvResponseDto> responseEntity = demandeRdvController.creerDemandeRdv(validRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("RDV-202609-XYZ123", responseEntity.getBody().getNumeroDossier());
        assertEquals("En attente", responseEntity.getBody().getStatut());
    }
}
