package com.eilco.ing2.hopital.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffectationMedecinRequestDto {

    @NotNull(message = "L'identifiant du médecin est obligatoire")
    private Long medecinId;

    @NotNull(message = "La date de la consultation est obligatoire")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateConsultation;

    @NotNull(message = "L'heure de la consultation est obligatoire (ex: 14:00)")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime heureConsultation;
}
