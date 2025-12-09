package com.springbootTemplate.univ.soa.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Représentation complète d'une recette")
public class RecetteResponse {

    @Schema(description = "Identifiant unique de la recette", example = "1")
    private Long id;

    @Schema(description = "Titre de la recette", example = "Pâtes Carbonara")
    private String titre;

    @Schema(description = "Description détaillée", example = "Une recette traditionnelle italienne")
    private String description;

    @Schema(description = "Temps total de préparation en minutes", example = "30")
    private Integer tempsTotal;

    @Schema(description = "Nombre de calories", example = "500")
    private Integer kcal;

    @Schema(description = "URL de l'image", example = "https://example.com/carbonara.jpg")
    private String imageUrl;

    @Schema(description = "Niveau de difficulté", example = "MOYEN")
    private String difficulte;

    @Schema(description = "Catégorie de la recette", example = "PLAT_PRINCIPAL")
    private String categorie;

    @Schema(description = "Date de création", example = "2025-01-15T14:30:00")
    private LocalDateTime dateCreation;

    @Schema(description = "Date de dernière modification", example = "2025-01-20T10:15:00")
    private LocalDateTime dateModification;

    @Schema(description = "Liste des ingrédients")
    private List<IngredientResponse> ingredients;

    @Schema(description = "Liste des étapes de préparation")
    private List<EtapeResponse> etapes;

    @Schema(description = "Tags associés", example = "[\"végétarien\", \"rapide\"]")
    private List<String> tags;

    @Schema(description = "Note moyenne de la recette", example = "4.5")
    private Double noteMoyenne;

    @Schema(description = "Nombre total de feedbacks", example = "25")
    private Integer nombreFeedbacks;
}
