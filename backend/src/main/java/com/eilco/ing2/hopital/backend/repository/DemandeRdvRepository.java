package com.eilco.ing2.hopital.backend.repository;

import com.eilco.ing2.hopital.backend.model.DemandeRdv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DemandeRdvRepository extends JpaRepository<DemandeRdv, Long> {

    Optional<DemandeRdv> findByNumeroDossier(String numeroDossier);

    boolean existsByNumeroDossier(String numeroDossier);
}
