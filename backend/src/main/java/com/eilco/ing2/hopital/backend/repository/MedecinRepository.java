package com.eilco.ing2.hopital.backend.repository;

import com.eilco.ing2.hopital.backend.model.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {

    Optional<Medecin> findByMatricule(String matricule);

    boolean existsByMatricule(String matricule);

    List<Medecin> findBySpecialiteIgnoreCaseAndActifTrue(String specialite);

    List<Medecin> findByDepartementIgnoreCaseAndActifTrue(String departement);

    List<Medecin> findByActifTrue();
}
