package com.eilco.ing2.hopital.backend.service.impl;

import com.eilco.ing2.hopital.backend.dto.ReferentielDepartementDto;
import com.eilco.ing2.hopital.backend.service.ReferentielService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ReferentielServiceImpl implements ReferentielService {

    @Override
    public List<ReferentielDepartementDto> getReferentielSpecialites() {
        return Arrays.asList(
                ReferentielDepartementDto.builder()
                        .nom("Services d'Urgences")
                        .specialites(Arrays.asList(
                                "Urgence adulte",
                                "Urgence pédiatrique",
                                "Urgence traumatologique"
                        ))
                        .build(),
                ReferentielDepartementDto.builder()
                        .nom("Services Cardiologiques")
                        .specialites(Arrays.asList(
                                "Cardiologie interventionnelle",
                                "Rythmologie cardiaque",
                                "Insuffisance cardiaque"
                        ))
                        .build(),
                ReferentielDepartementDto.builder()
                        .nom("Services de Chirurgie Générale")
                        .specialites(Arrays.asList(
                                "Chirurgie digestive et viscérale",
                                "Chirurgie pariétale",
                                "Chirurgie de provenance"
                        ))
                        .build()
        );
    }
}
