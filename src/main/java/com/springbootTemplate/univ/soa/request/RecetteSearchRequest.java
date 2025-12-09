package com.springbootTemplate.univ.soa.request;

import com.springbootTemplate.univ.soa.response.DifficulteEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Critères de recherche de recettes")
public class RecetteSearchRequest {

    @Schema(description = "Mot-clé à rechercher dans le titre ou description", example = "pâtes")
    private String keyword;

    @Schema(description = "Catégorie de recette", example = "PLAT_PRINCIPAL")
    private String categorie;

    @Schema(description = "Difficulté maximale", example = "MOYEN")
    private DifficulteEnum difficulteMax;

    @Schema(description = "Temps maximum en minutes", example = "45")
    @Min(value = 1, message = "Le temps maximum doit être supérieur à 0")
    private Integer tempsMax;

    @Schema(description = "Calories maximales", example = "600")
    @Min(value = 0, message = "Les calories ne peuvent pas être négatives")
    private Integer kcalMax;

    @Schema(description = "Liste d'ingrédients à inclure", example = "[1, 5, 12]")
    private List<Long> ingredientsInclus;

    @Schema(description = "Liste d'ingrédients à exclure", example = "[7, 9]")
    private List<Long> ingredientsExclus;

    @Schema(description = "Tags à rechercher", example = "[\"végétarien\", \"rapide\"]")
    private List<String> tags;

    @Schema(description = "Note moyenne minimale", example = "4.0", minimum = "1.0", maximum = "5.0")
    @DecimalMin(value = "1.0", message = "La note minimale doit être au moins 1.0")
    @DecimalMax(value = "5.0", message = "La note maximale ne peut pas dépasser 5.0")
    private Double noteMoyenneMin;
}
