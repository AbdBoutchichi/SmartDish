package com.springbootTemplate.univ.soa.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Étape de préparation")
public class EtapeResponse {

    @Schema(description = "ID de l'étape", example = "1")
    private Long id;

    @Schema(description = "Ordre de l'étape", example = "1")
    private Integer ordre;

    @Schema(description = "Temps estimé en minutes", example = "10")
    private Integer temps;

    @Schema(description = "Description de l'étape", example = "Faire cuire les pâtes")
    private String texte;
}
