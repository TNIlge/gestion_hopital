package com.eilco.ing2.hopital.backend.service;

import com.eilco.ing2.hopital.backend.dto.ConsultationPlanningDto;
import com.eilco.ing2.hopital.backend.dto.MedecinDto;
import com.eilco.ing2.hopital.backend.dto.PointagePresenceRequestDto;
import com.eilco.ing2.hopital.backend.exception.ResourceNotFoundException;
import com.eilco.ing2.hopital.backend.model.DemandeRdv;
import com.eilco.ing2.hopital.backend.model.Medecin;
import com.eilco.ing2.hopital.backend.model.StatutDemande;
import com.eilco.ing2.hopital.backend.model.StatutPresence;
import com.eilco.ing2.hopital.backend.repository.DemandeRdvRepository;
import com.eilco.ing2.hopital.backend.repository.MedecinRepository;
import com.eilco.ing2.hopital.backend.service.impl.MedecinServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedecinServiceTest {

    @Mock
    private MedecinRepository medecinRepository;

    @Mock
    private DemandeRdvRepository demandeRdvRepository;

    @InjectMocks
    private MedecinServiceImpl medecinService;

    private Medecin medecin;
    private DemandeRdv rdvValide;

    @BeforeEach
    void setUp() {
        medecin = Medecin.builder()
                .id(1L)
                .matricule("MED-007")
                .nom("Lefebvre")
                .prenom("Pierre")
                .departement("Services Cardiologiques")
                .specialite("Cardiologie interventionnelle")
                .email("pierre.lefebvre@hopital.fr")
                .telephone("0140020001")
                .actif(true)
                .build();

        rdvValide = DemandeRdv.builder()
                .id(50L)
                .numeroDossier("RDV-202610-PLAN01")
                .nom("Dubois")
                .prenom("Emma")
                .dateNaissance(LocalDate.of(1998, 3, 8))
                .statut(StatutDemande.ACCEPTEE)
                .medecin(medecin)
                .dateConsultation(LocalDate.of(2026, 10, 15))
                .heureConsultation(LocalTime.of(14, 30))
                .statutPresence(StatutPresence.NON_DEFINI)
                .specialite("Cardiologie interventionnelle")
                .dateCreation(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("CARE-301 : Doit identifier le médecin avec son matricule unique")
    void identifierParMatricule_Succes() {
        when(medecinRepository.findByMatricule("MED-007")).thenReturn(Optional.of(medecin));

        MedecinDto dto = medecinService.identifierParMatricule("MED-007");

        assertThat(dto).isNotNull();
        assertThat(dto.getMatricule()).isEqualTo("MED-007");
        assertThat(dto.getNom()).isEqualTo("Lefebvre");
    }

    @Test
    @DisplayName("CARE-301 : Doit renvoyer 404 si le matricule n'existe pas")
    void identifierParMatricule_Inexistant_DoitEchouer() {
        when(medecinRepository.findByMatricule("MED-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medecinService.identifierParMatricule("MED-999"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("CARE-302 : Étanchéité stricte - Le médecin ne voit que ses rendez-vous acceptés")
    void getPlanningMedecin_Succes() {
        LocalDate date = LocalDate.of(2026, 10, 15);
        when(medecinRepository.existsByMatricule("MED-007")).thenReturn(true);
        when(demandeRdvRepository.findPlanningByMedecinAndDate("MED-007", date))
                .thenReturn(Collections.singletonList(rdvValide));

        List<ConsultationPlanningDto> planning = medecinService.getPlanningMedecin("MED-007", date);

        assertThat(planning).hasSize(1);
        assertThat(planning.get(0).getNumeroDossier()).isEqualTo("RDV-202610-PLAN01");
        assertThat(planning.get(0).getNomPatient()).isEqualTo("Dubois");
    }

    @Test
    @DisplayName("CARE-304 : Pointage présence patient - Marquer Présent et clôturer le dossier")
    void pointerPresence_Present_Succes() {
        when(demandeRdvRepository.findById(50L)).thenReturn(Optional.of(rdvValide));
        when(demandeRdvRepository.save(any(DemandeRdv.class))).thenAnswer(i -> i.getArgument(0));

        PointagePresenceRequestDto request = new PointagePresenceRequestDto(StatutPresence.PRESENT);
        ConsultationPlanningDto result = medecinService.pointerPresence(50L, request);

        assertThat(result.getStatutPresence()).isEqualTo(StatutPresence.PRESENT);
        assertThat(rdvValide.getStatut()).isEqualTo(StatutDemande.ACCEPTEE);
        assertThat(rdvValide.getDatePointage()).isNotNull();

        verify(demandeRdvRepository).save(rdvValide);
    }
}
