package com.eilco.ing2.hopital.backend.service;

import com.eilco.ing2.hopital.backend.dto.ReferentielDepartementDto;

import java.util.List;

public interface ReferentielService {
    /**
     * CARE-001 : Récupère la liste complète des 3 départements et de leurs 9 spécialités exactes.
     */
    List<ReferentielDepartementDto> getReferentielSpecialites();
}
