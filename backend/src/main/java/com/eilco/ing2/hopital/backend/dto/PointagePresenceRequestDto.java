package com.eilco.ing2.hopital.backend.dto;

import com.eilco.ing2.hopital.backend.model.StatutPresence;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointagePresenceRequestDto {

    @NotNull(message = "Le statut de présence est obligatoire (PRESENT ou ABSENT)")
    private StatutPresence presence;
}
