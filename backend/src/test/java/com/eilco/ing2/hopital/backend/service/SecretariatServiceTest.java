package com.eilco.ing2.hopital.backend.service;

import com.eilco.ing2.hopital.backend.dto.AffectationMedecinRequestDto;
import com.eilco.ing2.hopital.backend.dto.DemandeRdvAdminDto;
import com.eilco.ing2.hopital.backend.dto.RefusDemandeRequestDto;
import com.eilco.ing2.hopital.backend.exception.AgendaConflictException;
import com.eilco.ing2.hopital.backend.exception.BusinessValidationException;
import com.eilco.ing2.hopital.backend.model.DemandeRdv;
import com.eilco.ing2.hopital.backend.model.Medecin;
import com.eilco.ing2.hopital.backend.model.StatutDemande;
import com.eilco.ing2.hopital.backend.repository.DemandeRdvRepository;
import com.eilco.ing2.hopital.backend.repository.MedecinRepository;
import com.eilco.ing2.hopital.backend.service.impl.SecretariatServiceImpl;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecretariatServiceTest {

    @Mock
    private DemandeRdvRepository demandeRdvRepository;

    @Mock
    private MedecinRepository medecinRepository;

    @InjectMocks
    private SecretariatServiceImpl secretariatService;

    private DemandeRdv demande;
    private Medecin medecinCardio;

    @BeforeEach
    void setUp() {
        demande = DemandeRdv.builder()
                .id(10L)
                .numeroDossier("RDV-202610-123456")
                .nom("Martin")
                .prenom("Alice")
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .numeroSecuriteSociale("martin-alice-19900101")
                .departement("Services Cardiologiques")
                .specialite("Cardiologie interventionnelle")
                .dateSouhaitee(LocalDate.now().plusDays(4))
                .statut(StatutDemande.EN_COURS)
                .dateCreation(LocalDateTime.now())
                .build();

        medecinCardio = Medecin.builder()
                .id(1L)
                .matricule("MED-007")
                .nom("Lefebvre")
                .prenom("Pierre")
                .departement("Services Cardiologiques")
                .specialite("Cardiologie interventionnelle")
                .actif(true)
                .build();
    }

    @Test
    @DisplayName("CARE-203 : Doit passer une demande au statut 'Analysée'")
    void passerEnAnalysee_Succes() {
        when(demandeRdvRepository.findById(10L)).thenReturn(Optional.of(demande));
        when(demandeRdvRepository.save(any(DemandeRdv.class))).thenAnswer(i -> i.getArgument(0));

        DemandeRdvAdminDto result = secretariatService.passerEnAnalysee(10L);

        assertThat(result.getStatut()).isEqualTo(StatutDemande.ANALYSEE);
        verify(demandeRdvRepository).save(demande);
    }

    @Test
    @DisplayName("CARE-203 : Doit refuser la demande et enregistrer le motif explicite")
    void refuserDemande_AvecMotif_Succes() {
        when(demandeRdvRepository.findById(10L)).thenReturn(Optional.of(demande));
        when(demandeRdvRepository.save(any(DemandeRdv.class))).thenAnswer(i -> i.getArgument(0));

        RefusDemandeRequestDto refusDto = new RefusDemandeRequestDto("Dossier incomplet et créneaux indisponibles");
        DemandeRdvAdminDto result = secretariatService.refuserDemande(10L, refusDto);

        assertThat(result.getStatut()).isEqualTo(StatutDemande.REFUSEE);
        assertThat(result.getMotifRefus()).isEqualTo("Dossier incomplet et créneaux indisponibles");
    }

    @Test
    @DisplayName("CARE-203 : Doit rejeter le refus si le motif est vide ou manquant")
    void refuserDemande_SansMotif_DoitEchouer() {
        when(demandeRdvRepository.findById(10L)).thenReturn(Optional.of(demande));

        RefusDemandeRequestDto refusVide = new RefusDemandeRequestDto("   ");

        assertThatThrownBy(() -> secretariatService.refuserDemande(10L, refusVide))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("motif explicite");
    }

    @Test
    @DisplayName("CARE-205 : Algorithme Anti-Conflit - Doit lever AgendaConflictException en cas de double réservation")
    void affecterMedecin_ConflitAgenda_DoitLeverException400() {
        LocalDate date = LocalDate.now().plusDays(2);
        LocalTime heure = LocalTime.of(14, 0);

        when(demandeRdvRepository.findById(10L)).thenReturn(Optional.of(demande));
        when(medecinRepository.findById(1L)).thenReturn(Optional.of(medecinCardio));

        // Simulation : le médecin a déjà une consultation sur ce créneau
        when(demandeRdvRepository.existsByMedecinIdAndDateConsultationAndHeureConsultationAndStatutAndIdNot(
                1L, date, heure, StatutDemande.VALIDEE, 10L
        )).thenReturn(true);

        AffectationMedecinRequestDto affectation = new AffectationMedecinRequestDto(1L, date, heure);

        assertThatThrownBy(() -> secretariatService.affecterMedecin(10L, affectation))
                .isInstanceOf(AgendaConflictException.class)
                .hasMessage("Conflit d'agenda : Le médecin sélectionné a déjà une consultation sur ce créneau.");
    }

    @Test
    @DisplayName("CARE-206 : Doit affecter le médecin libre et passer la demande au statut 'Validée'")
    void affecterMedecin_Succes() {
        LocalDate date = LocalDate.now().plusDays(2);
        LocalTime heure = LocalTime.of(15, 0);

        when(demandeRdvRepository.findById(10L)).thenReturn(Optional.of(demande));
        when(medecinRepository.findById(1L)).thenReturn(Optional.of(medecinCardio));
        when(demandeRdvRepository.existsByMedecinIdAndDateConsultationAndHeureConsultationAndStatutAndIdNot(
                1L, date, heure, StatutDemande.VALIDEE, 10L
        )).thenReturn(false);
        when(demandeRdvRepository.save(any(DemandeRdv.class))).thenAnswer(i -> i.getArgument(0));

        AffectationMedecinRequestDto affectation = new AffectationMedecinRequestDto(1L, date, heure);
        DemandeRdvAdminDto result = secretariatService.affecterMedecin(10L, affectation);

        assertThat(result.getStatut()).isEqualTo(StatutDemande.VALIDEE);
        assertThat(result.getMedecinId()).isEqualTo(1L);
        assertThat(result.getDateConsultation()).isEqualTo(date);
        assertThat(result.getHeureConsultation()).isEqualTo(heure);
    }
}
