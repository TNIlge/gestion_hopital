package com.eilco.ing2.hopital.backend.config;

import com.eilco.ing2.hopital.backend.model.DemandeRdv;
import com.eilco.ing2.hopital.backend.model.Medecin;
import com.eilco.ing2.hopital.backend.model.StatutDemande;
import com.eilco.ing2.hopital.backend.model.StatutPresence;
import com.eilco.ing2.hopital.backend.repository.DemandeRdvRepository;
import com.eilco.ing2.hopital.backend.repository.MedecinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final MedecinRepository medecinRepository;
    private final DemandeRdvRepository demandeRdvRepository;

    @Override
    public void run(String... args) {
        initialiserMedecins();
        initialiserDemandesExemples();
    }

    private void initialiserMedecins() {
        if (medecinRepository.count() > 0) {
            log.info("Les médecins sont déjà initialisés en BDD.");
            return;
        }

        log.info("Initialisation du référentiel des médecins de l'hôpital...");

        List<Medecin> medecins = Arrays.asList(
                // Services d'Urgences
                Medecin.builder().matricule("MED-001").nom("Durand").prenom("Sophie").departement("Services d'Urgences").specialite("Urgence adulte").email("sophie.durand@hopital.fr").telephone("0140010001").build(),
                Medecin.builder().matricule("MED-002").nom("Moreau").prenom("Alexandre").departement("Services d'Urgences").specialite("Urgence adulte").email("alexandre.moreau@hopital.fr").telephone("0140010002").build(),
                Medecin.builder().matricule("MED-003").nom("Petit").prenom("Claire").departement("Services d'Urgences").specialite("Urgence pédiatrique").email("claire.petit@hopital.fr").telephone("0140010003").build(),
                Medecin.builder().matricule("MED-004").nom("Rousseau").prenom("Marc").departement("Services d'Urgences").specialite("Urgence pédiatrique").email("marc.rousseau@hopital.fr").telephone("0140010004").build(),
                Medecin.builder().matricule("MED-005").nom("Blanc").prenom("Julien").departement("Services d'Urgences").specialite("Urgence traumatologique").email("julien.blanc@hopital.fr").telephone("0140010005").build(),
                Medecin.builder().matricule("MED-006").nom("Guerin").prenom("Nathalie").departement("Services d'Urgences").specialite("Urgence traumatologique").email("nathalie.guerin@hopital.fr").telephone("0140010006").build(),

                // Services Cardiologiques
                Medecin.builder().matricule("MED-007").nom("Lefebvre").prenom("Pierre").departement("Services Cardiologiques").specialite("Cardiologie interventionnelle").email("pierre.lefebvre@hopital.fr").telephone("0140020001").build(),
                Medecin.builder().matricule("MED-008").nom("Girard").prenom("Camille").departement("Services Cardiologiques").specialite("Cardiologie interventionnelle").email("camille.girard@hopital.fr").telephone("0140020002").build(),
                Medecin.builder().matricule("MED-009").nom("Mercier").prenom("David").departement("Services Cardiologiques").specialite("Rythmologie cardiaque").email("david.mercier@hopital.fr").telephone("0140020003").build(),
                Medecin.builder().matricule("MED-010").nom("Faure").prenom("Emilie").departement("Services Cardiologiques").specialite("Rythmologie cardiaque").email("emilie.faure@hopital.fr").telephone("0140020004").build(),
                Medecin.builder().matricule("MED-011").nom("Roux").prenom("Antoine").departement("Services Cardiologiques").specialite("Insuffisance cardiaque").email("antoine.roux@hopital.fr").telephone("0140020005").build(),
                Medecin.builder().matricule("MED-012").nom("Garnier").prenom("Helene").departement("Services Cardiologiques").specialite("Insuffisance cardiaque").email("helene.garnier@hopital.fr").telephone("0140020006").build(),

                // Services de Chirurgie Générale
                Medecin.builder().matricule("MED-013").nom("Bonnet").prenom("Luc").departement("Services de Chirurgie Générale").specialite("Chirurgie digestive et viscérale").email("luc.bonnet@hopital.fr").telephone("0140030001").build(),
                Medecin.builder().matricule("MED-014").nom("Dupuis").prenom("Isabelle").departement("Services de Chirurgie Générale").specialite("Chirurgie digestive et viscérale").email("isabelle.dupuis@hopital.fr").telephone("0140030002").build(),
                Medecin.builder().matricule("MED-015").nom("Fontaine").prenom("Thomas").departement("Services de Chirurgie Générale").specialite("Chirurgie pariétale").email("thomas.fontaine@hopital.fr").telephone("0140030003").build(),
                Medecin.builder().matricule("MED-016").nom("Chevalier").prenom("Sarah").departement("Services de Chirurgie Générale").specialite("Chirurgie pariétale").email("sarah.chevalier@hopital.fr").telephone("0140030004").build(),
                Medecin.builder().matricule("MED-017").nom("Lambert").prenom("Guillaume").departement("Services de Chirurgie Générale").specialite("Chirurgie de provenance").email("guillaume.lambert@hopital.fr").telephone("0140030005").build(),
                Medecin.builder().matricule("MED-018").nom("Vidal").prenom("Aurore").departement("Services de Chirurgie Générale").specialite("Chirurgie de provenance").email("aurore.vidal@hopital.fr").telephone("0140030006").build()
        );

        medecinRepository.saveAll(medecins);
        log.info("18 médecins ont été initialisés avec succès.");
    }

    private void initialiserDemandesExemples() {
        if (demandeRdvRepository.count() > 0) {
            return;
        }

        log.info("Initialisation de demandes de rendez-vous de démonstration...");

        Medecin medecinCardio = medecinRepository.findByMatricule("MED-007").orElse(null);

        // 1. Demande 'En cours' (non traitée)
        DemandeRdv demande1 = DemandeRdv.builder()
                .numeroDossier("RDV-202610-A1B2C3")
                .nom("Martin")
                .prenom("Alice")
                .dateNaissance(LocalDate.of(1992, 5, 14))
                .numeroSecuriteSociale("martin-alice-19920514")
                .departement("Services Cardiologiques")
                .specialite("Cardiologie interventionnelle")
                .dateSouhaitee(LocalDate.now().plusDays(3))
                .motif("Douleurs thoraciques lors de l'effort")
                .statut(StatutDemande.EN_COURS)
                .statutPresence(StatutPresence.NON_DEFINI)
                .dateCreation(LocalDateTime.now().minusHours(4))
                .dateMiseAJour(LocalDateTime.now().minusHours(4))
                .build();

        // 2. Demande 'Analysée' par le secrétariat
        DemandeRdv demande2 = DemandeRdv.builder()
                .numeroDossier("RDV-202610-D4E5F6")
                .nom("Bernard")
                .prenom("Lucas")
                .dateNaissance(LocalDate.of(1985, 11, 20))
                .numeroSecuriteSociale("bernard-lucas-19851120")
                .departement("Services d'Urgences")
                .specialite("Urgence traumatologique")
                .dateSouhaitee(LocalDate.now().plusDays(1))
                .motif("Contusion cheville droite suite à chute")
                .statut(StatutDemande.ANALYSEE)
                .statutPresence(StatutPresence.NON_DEFINI)
                .dateCreation(LocalDateTime.now().minusHours(2))
                .dateMiseAJour(LocalDateTime.now().minusHours(1))
                .build();

        // 3. Demande 'Validée' avec médecin affecté et créneau fixé
        DemandeRdv demande3 = DemandeRdv.builder()
                .numeroDossier("RDV-202610-G7H8I9")
                .nom("Dubois")
                .prenom("Emma")
                .dateNaissance(LocalDate.of(1998, 3, 8))
                .numeroSecuriteSociale("dubois-emma-19980308")
                .departement("Services Cardiologiques")
                .specialite("Cardiologie interventionnelle")
                .dateSouhaitee(LocalDate.now().plusDays(2))
                .motif("Contrôle annuel post-opératoire")
                .statut(StatutDemande.VALIDEE)
                .medecin(medecinCardio)
                .dateConsultation(LocalDate.now().plusDays(2))
                .heureConsultation(LocalTime.of(14, 30))
                .statutPresence(StatutPresence.NON_DEFINI)
                .dateCreation(LocalDateTime.now().minusDays(1))
                .dateMiseAJour(LocalDateTime.now().minusHours(5))
                .build();

        demandeRdvRepository.saveAll(Arrays.asList(demande1, demande2, demande3));
        log.info("Demandes de test initialisées avec succès.");
    }
}
