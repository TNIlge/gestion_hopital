package com.eilco.ing2.hopital.backend.service;

import com.eilco.ing2.hopital.backend.dto.DemandeRdvRequestDto;
import com.eilco.ing2.hopital.backend.dto.DemandeRdvResponseDto;
import com.eilco.ing2.hopital.backend.exception.InvalidNssException;
import com.eilco.ing2.hopital.backend.model.DemandeRdv;
import com.eilco.ing2.hopital.backend.model.StatutDemande;
import com.eilco.ing2.hopital.backend.repository.DemandeRdvRepository;
import com.eilco.ing2.hopital.backend.service.impl.DemandeRdvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
                .specialite("Rythmologie cardiaque")
                .dateSouhaitee(LocalDate.of(2026, 11, 15))
                .motif("Consultation de routine")
                .build();
    }

    @Test
    @DisplayName("Création nominale : statut initial 'En attente' et identifiant unique généré")
    void shouldCreateDemandeWithStatusEnAttente() {
        when(demandeRdvRepository.existsByNumeroDossier(anyString())).thenReturn(false);
        when(demandeRdvRepository.save(any(DemandeRdv.class))).thenAnswer(invocation -> {
            DemandeRdv entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        DemandeRdvResponseDto response = demandeRdvService.creerDemandeRdv(validRequest);

        assertNotNull(response);
        assertNotNull(response.getNumeroDossier());
        assertTrue(response.getNumeroDossier().startsWith("RDV-"));
        assertEquals("En attente", response.getStatut());
        assertEquals("Dupont", response.getNom());
        assertEquals("Jean", response.getPrenom());

        ArgumentCaptor<DemandeRdv> captor = ArgumentCaptor.forClass(DemandeRdv.class);
        verify(demandeRdvRepository, times(1)).save(captor.capture());
        DemandeRdv saved = captor.getValue();
        assertEquals(StatutDemande.EN_ATTENTE, saved.getStatut());
        assertEquals("dupont-jean-19951024", saved.getNumeroSecuriteSociale());
    }

    @Test
    @DisplayName("Blocage si le N° SS ne correspond pas au format nom-prenom-YYYYMMDD")
    void shouldThrowExceptionWhenNssIsInvalid() {
        validRequest.setNumeroSecuriteSociale("martin-pierre-19800101");

        assertThrows(InvalidNssException.class, () -> {
            demandeRdvService.creerDemandeRdv(validRequest);
        });

        verify(demandeRdvRepository, never()).save(any());
    }
}
