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
@Schema(description = "Statistiques d'une recette")
public class RecetteStatsResponse {

    @Schema(description = "ID de la recette", example = "1")
    private Long recetteId;

    @Schema(description = "Titre de la recette", example = "Pâtes Carbonara")
    private String titre;

    @Schema(description = "Note moyenne", example = "4.5")
    private Double noteMoyenne;

    @Schema(description = "Nombre total de feedbacks", example = "25")
    private Integer nombreFeedbacks;

    @Schema(description = "Nombre de vues", example = "150")
    private Integer nombreVues;

    @Schema(description = "Nombre de favoris", example = "42")
    private Integer nombreFavoris;

    @Schema(description = "Distribution des notes (1 à 5 étoiles)")
    private DistributionNotesResponse distributionNotes;
}
