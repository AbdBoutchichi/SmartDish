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
@Schema(description = "Distribution des notes par étoiles")
public class DistributionNotesResponse {

    @Schema(description = "Nombre de notes 1 étoile", example = "2")
    private Integer note1;

    @Schema(description = "Nombre de notes 2 étoiles", example = "3")
    private Integer note2;

    @Schema(description = "Nombre de notes 3 étoiles", example = "5")
    private Integer note3;

    @Schema(description = "Nombre de notes 4 étoiles", example = "8")
    private Integer note4;

    @Schema(description = "Nombre de notes 5 étoiles", example = "7")
    private Integer note5;
}
