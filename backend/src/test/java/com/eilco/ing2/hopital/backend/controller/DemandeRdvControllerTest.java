package com.eilco.ing2.hopital.backend.controller;

import com.eilco.ing2.hopital.backend.dto.DemandeRdvRequestDto;
import com.eilco.ing2.hopital.backend.dto.DemandeRdvResponseDto;
import com.eilco.ing2.hopital.backend.dto.SuiviDemandeResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemandeRdvControllerTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("CARE-103 & CARE-104 & CARE-105 : Cycle complet soumission et suivi de demande")
    void testSoumissionEtSuiviDemande() {
        String baseUrl = "http://localhost:" + port + "/api/v1/rendez-vous";

        DemandeRdvRequestDto request = DemandeRdvRequestDto.builder()
                .nom("Lemoine")
                .prenom("Sarah")
                .dateNaissance(LocalDate.of(1993, 7, 12))
                .numeroSecuriteSociale("lemoine-sarah-19930712")
                .departement("Services Cardiologiques")
                .specialite("Rythmologie cardiaque")
                .dateSouhaitee(LocalDate.now().plusDays(10))
                .motif("Palpitations")
                .build();

        // 1. Soumission POST
        ResponseEntity<DemandeRdvResponseDto> responseCreation = restTemplate.postForEntity(
                baseUrl,
                request,
                DemandeRdvResponseDto.class
        );

        assertThat(responseCreation.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        DemandeRdvResponseDto bodyCreation = responseCreation.getBody();
        assertThat(bodyCreation).isNotNull();
        assertThat(bodyCreation.getNumeroDossier()).isNotBlank();
        assertThat(bodyCreation.getStatut()).isEqualTo("En cours");

        // 2. Suivi GET
        String suiviUrl = baseUrl + "/suivi/" + bodyCreation.getNumeroDossier();
        ResponseEntity<SuiviDemandeResponseDto> responseSuivi = restTemplate.getForEntity(
                suiviUrl,
                SuiviDemandeResponseDto.class
        );

        assertThat(responseSuivi.getStatusCode()).isEqualTo(HttpStatus.OK);
        SuiviDemandeResponseDto bodySuivi = responseSuivi.getBody();
        assertThat(bodySuivi).isNotNull();
        assertThat(bodySuivi.getNumeroDossier()).isEqualTo(bodyCreation.getNumeroDossier());
        assertThat(bodySuivi.getStatut()).isEqualTo("En cours");
    }

    @Test
    @DisplayName("CARE-102 : Rejet HTTP 400 si NSS invalide")
    void testSoumissionNssInvalide() {
        String baseUrl = "http://localhost:" + port + "/api/v1/rendez-vous";

        DemandeRdvRequestDto request = DemandeRdvRequestDto.builder()
                .nom("Martin")
                .prenom("Paul")
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .numeroSecuriteSociale("nss-totalement-invalide")
                .departement("Services d'Urgences")
                .specialite("Urgence adulte")
                .dateSouhaitee(LocalDate.now().plusDays(1))
                .motif("Forte fièvre")
                .build();

        assertThatThrownBy(() -> restTemplate.postForEntity(baseUrl, request, DemandeRdvResponseDto.class))
                .isInstanceOf(HttpClientErrorException.BadRequest.class);
    }
}
