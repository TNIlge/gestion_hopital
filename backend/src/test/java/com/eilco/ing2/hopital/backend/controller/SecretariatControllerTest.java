package com.eilco.ing2.hopital.backend.controller;

import com.eilco.ing2.hopital.backend.model.DemandeRdv;
import com.eilco.ing2.hopital.backend.model.Medecin;
import com.eilco.ing2.hopital.backend.model.StatutDemande;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SecretariatControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DemandeRdvRepository demandeRdvRepository;

    @Autowired
    private MedecinRepository medecinRepository;

    private MockMvc mockMvc;
    private DemandeRdv testDemande;
    private Medecin testMedecin;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        testMedecin = medecinRepository.findByMatricule("MED-007")
                .orElseGet(() -> medecinRepository.save(Medecin.builder()
                        .matricule("MED-007")
                        .nom("Lefebvre")
                        .prenom("Pierre")
                        .departement("Services Cardiologiques")
                        .specialite("Cardiologie interventionnelle")
                        .actif(true)
                        .build()));

        testDemande = demandeRdvRepository.save(DemandeRdv.builder()
                .numeroDossier("RDV-SECRETARIAT-TEST-" + System.currentTimeMillis())
                .nom("Moreau")
                .prenom("Julie")
                .dateNaissance(LocalDate.of(1991, 4, 18))
                .numeroSecuriteSociale("moreau-julie-19910418")
                .departement("Services Cardiologiques")
                .specialite("Cardiologie interventionnelle")
                .dateSouhaitee(LocalDate.now().plusDays(5))
                .motif("Consultation test secrétariat")
                .statut(StatutDemande.EN_ATTENTE)
                .dateCreation(LocalDateTime.now())
                .build());
    }

    @Test
    @DisplayName("CARE-201 & CARE-202 : Listing des demandes avec filtre par statut")
    void testGetDemandesAvecFiltre() throws Exception {
        mockMvc.perform(get("/api/v1/secretariat/demandes")
                        .param("statut", "EN_ATTENTE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("CARE-203 : Refus d'une demande avec motif via PATCH (Déclinée)")
    void testRefuserDemande() throws Exception {
        String jsonRefus = "{\"motifRefus\":\"Créneau non disponible ce mois-ci\"}";

        mockMvc.perform(patch("/api/v1/secretariat/demandes/" + testDemande.getId() + "/refuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRefus))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut", is("DECLINEE")))
                .andExpect(jsonPath("$.motifRefus", is("Créneau non disponible ce mois-ci")));
    }

    @Test
    @DisplayName("CARE-204 : Récupérer les médecins filtrés par spécialité")
    void testGetMedecinsParSpecialite() throws Exception {
        mockMvc.perform(get("/api/v1/secretariat/medecins")
                        .param("specialite", "Cardiologie interventionnelle")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("CARE-205 & CARE-206 : Affectation médecin et gestion anti-conflit (Acceptée)")
    void testAffectationEtAntiConflit() throws Exception {
        LocalDate date = LocalDate.now().plusDays(25);
        String jsonAffectation = String.format(
                "{\"medecinId\":%d,\"dateConsultation\":\"%s\",\"heureConsultation\":\"16:00\"}",
                testMedecin.getId(), date.toString()
        );

        // 1. Première affectation -> Succès HTTP 200 (CARE-206)
        mockMvc.perform(post("/api/v1/secretariat/demandes/" + testDemande.getId() + "/affecter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAffectation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut", is("ACCEPTEE")));

        // 2. Création d'une deuxième demande
        DemandeRdv demande2 = demandeRdvRepository.save(DemandeRdv.builder()
                .numeroDossier("RDV-CONFLIT-TEST-" + System.currentTimeMillis())
                .nom("Rousseau")
                .prenom("David")
                .dateNaissance(LocalDate.of(1988, 2, 2))
                .numeroSecuriteSociale("rousseau-david-19880202")
                .departement("Services Cardiologiques")
                .specialite("Cardiologie interventionnelle")
                .dateSouhaitee(date)
                .statut(StatutDemande.EN_ATTENTE)
                .dateCreation(LocalDateTime.now())
                .build());

        // 3. Deuxième affectation sur le MÊME créneau et même médecin -> Doit renvoyer HTTP 400 (CARE-205)
        mockMvc.perform(post("/api/v1/secretariat/demandes/" + demande2.getId() + "/affecter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAffectation))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Conflit d'agenda")));
    }
}
