package com.eilco.ing2.hopital.backend.dto;

import com.eilco.ing2.hopital.backend.model.StatutPresence;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationPlanningDto {

    private Long id;
    private String numeroDossier;
    private String nomPatient;
    private String prenomPatient;
    private LocalDate dateNaissancePatient;
    private String motif;
    private LocalDate dateConsultation;
    private LocalTime heureConsultation;
    private String specialite;
    private String statut;
    private StatutPresence statutPresence;
    private LocalDateTime datePointage;
}
