package com.eilco.ing2.hopital.backend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReferentielControllerTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("CARE-001 : GET /api/v1/referentiel/specialites doit renvoyer les 3 départements et 9 spécialités")
    void testGetReferentielSpecialites() {
        String url = "http://localhost:" + port + "/api/v1/referentiel/specialites";
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        List<Map<String, Object>> departements = response.getBody();
        assertThat(departements).hasSize(3);

        int totalSpecialites = departements.stream()
                .mapToInt(d -> ((List<?>) d.get("specialites")).size())
                .sum();
        assertThat(totalSpecialites).isEqualTo(9);
    }
}
