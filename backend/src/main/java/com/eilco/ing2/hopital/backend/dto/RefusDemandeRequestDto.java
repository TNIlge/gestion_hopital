package com.eilco.ing2.hopital.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefusDemandeRequestDto {

    @NotBlank(message = "Le motif de refus est obligatoire pour passer une demande à l'état Refusée")
    private String motifRefus;
}
