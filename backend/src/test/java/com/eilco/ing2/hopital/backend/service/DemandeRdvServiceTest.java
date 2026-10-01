package com.eilco.ing2.hopital.backend.service;

import com.eilco.ing2.hopital.backend.dto.DemandeRdvRequestDto;
import com.eilco.ing2.hopital.backend.dto.DemandeRdvResponseDto;
import com.eilco.ing2.hopital.backend.dto.SuiviDemandeResponseDto;
import com.eilco.ing2.hopital.backend.exception.InvalidNssException;
import com.eilco.ing2.hopital.backend.exception.ResourceNotFoundException;
import com.eilco.ing2.hopital.backend.model.DemandeRdv;
import com.eilco.ing2.hopital.backend.model.StatutDemande;
import com.eilco.ing2.hopital.backend.repository.DemandeRdvRepository;
import com.eilco.ing2.hopital.backend.service.impl.DemandeRdvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DemandeRdvServiceTest {

    @Mock
    private DemandeRdvRepository demandeRdvRepository;

    @InjectMocks
    private DemandeRdvServiceImpl demandeRdvService;

    private DemandeRdvRequestDto validRequest;

    @BeforeEach
    void setUp() {
        validRequest = DemandeRdvRequestDto.builder()
                .nom("Dupont")
                .prenom("Jean")
                .dateNaissance(LocalDate.of(1995, 10, 24))
                .numeroSecuriteSociale("dupont-jean-19951024")
                .departement("Services Cardiologiques")
                .specialite("Cardiologie interventionnelle")
                .dateSouhaitee(LocalDate.now().plusDays(5))
                .motif("Consultation de routine")
                .build();
    }

    @Test
    @DisplayName("CARE-103 : Doit enregistrer une demande avec statut 'En cours' et numéro unique")
    void creerDemandeRdv_Succes() {
        when(demandeRdvRepository.existsByNumeroDossier(anyString())).thenReturn(false);
        when(demandeRdvRepository.save(any(DemandeRdv.class))).thenAnswer(invocation -> {
            DemandeRdv d = invocation.getArgument(0);
            d.setId(1L);
            d.setDateCreation(LocalDateTime.now());
            return d;
        });

        DemandeRdvResponseDto response = demandeRdvService.creerDemandeRdv(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getNumeroDossier()).startsWith("RDV-");
        assertThat(response.getStatut()).isEqualTo("En cours");
        assertThat(response.getNom()).isEqualTo("Dupont");
        assertThat(response.getPrenom()).isEqualTo("Jean");

        verify(demandeRdvRepository).save(any(DemandeRdv.class));
    }

    @Test
    @DisplayName("CARE-102 : Doit rejeter la création si le N° SS ne correspond pas au Nom/Prénom")
    void creerDemandeRdv_NssInvalide_DoitEchouer() {
        validRequest.setNumeroSecuriteSociale("autre-personne-19951024");

        assertThatThrownBy(() -> demandeRdvService.creerDemandeRdv(validRequest))
                .isInstanceOf(InvalidNssException.class);
    }

    @Test
    @DisplayName("CARE-105 : Doit renvoyer le suivi d'une demande existante")
    void getSuiviDemande_Existant_Succes() {
        DemandeRdv entity = DemandeRdv.builder()
                .id(1L)
                .numeroDossier("RDV-202610-TEST01")
                .nom("Dupont")
                .prenom("Jean")
                .statut(StatutDemande.EN_COURS)
                .departement("Services Cardiologiques")
                .specialite("Cardiologie interventionnelle")
                .dateSouhaitee(LocalDate.now().plusDays(2))
                .dateCreation(LocalDateTime.now())
                .build();

        when(demandeRdvRepository.findByNumeroDossier("RDV-202610-TEST01"))
                .thenReturn(Optional.of(entity));

        SuiviDemandeResponseDto suivi = demandeRdvService.getSuiviDemande("RDV-202610-TEST01");

        assertThat(suivi).isNotNull();
        assertThat(suivi.getNumeroDossier()).isEqualTo("RDV-202610-TEST01");
        assertThat(suivi.getStatut()).isEqualTo("En cours");
    }

    @Test
    @DisplayName("CARE-105 : Doit lever une exception 404 si le dossier de suivi n'existe pas")
    void getSuiviDemande_Inconnu_DoitEchouer() {
        when(demandeRdvRepository.findByNumeroDossier("RDV-INEXISTANT"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> demandeRdvService.getSuiviDemande("RDV-INEXISTANT"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
