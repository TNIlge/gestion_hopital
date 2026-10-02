package com.eilco.ing2.hopital.backend.controller;

import com.eilco.ing2.hopital.backend.model.DemandeRdv;
import com.eilco.ing2.hopital.backend.model.Medecin;
import com.eilco.ing2.hopital.backend.model.StatutDemande;
import com.eilco.ing2.hopital.backend.model.StatutPresence;
import com.eilco.ing2.hopital.backend.repository.DemandeRdvRepository;
import com.eilco.ing2.hopital.backend.repository.MedecinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MedecinControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DemandeRdvRepository demandeRdvRepository;

    @Autowired
    private MedecinRepository medecinRepository;

    private MockMvc mockMvc;
    private Medecin medecinTest;
    private DemandeRdv rdvValide;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        medecinTest = medecinRepository.findByMatricule("MED-001")
                .orElseGet(() -> medecinRepository.save(Medecin.builder()
                        .matricule("MED-001")
                        .nom("Durand")
                        .prenom("Sophie")
                        .departement("Services d'Urgences")
                        .specialite("Urgence adulte")
                        .actif(true)
                        .build()));

        rdvValide = demandeRdvRepository.save(DemandeRdv.builder()
                .numeroDossier("RDV-MED-TEST-" + System.currentTimeMillis())
                .nom("Gérard")
                .prenom("Luc")
                .dateNaissance(LocalDate.of(1980, 5, 20))
                .numeroSecuriteSociale("gerard-luc-19800520")
                .departement("Services d'Urgences")
                .specialite("Urgence adulte")
                .dateSouhaitee(LocalDate.now())
                .statut(StatutDemande.ACCEPTEE)
                .medecin(medecinTest)
                .dateConsultation(LocalDate.now())
                .heureConsultation(LocalTime.of(10, 0))
                .statutPresence(StatutPresence.NON_DEFINI)
                .dateCreation(LocalDateTime.now())
                .build());
    }

    @Test
    @DisplayName("CARE-301 : Authentification médecin par matricule unique")
    void testAuthMedecin() throws Exception {
        mockMvc.perform(get("/api/v1/medecin/profil/" + medecinTest.getMatricule())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricule", is(medecinTest.getMatricule())))
                .andExpect(jsonPath("$.nom", is(medecinTest.getNom())));
    }

    @Test
    @DisplayName("CARE-302 : Consultation du planning médecin avec étanchéité des données")
    void testGetPlanning() throws Exception {
        mockMvc.perform(get("/api/v1/medecin/" + medecinTest.getMatricule() + "/planning")
                        .param("date", LocalDate.now().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("CARE-304 : Pointage de présence patient (PUT) et clôture")
    void testPointerPresence() throws Exception {
        String jsonPresence = "{\"presence\":\"PRESENT\"}";

        mockMvc.perform(put("/api/v1/medecin/rendez-vous/" + rdvValide.getId() + "/presence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPresence))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statutPresence", is("PRESENT")))
                .andExpect(jsonPath("$.statut", is("Acceptée")));
    }
}
