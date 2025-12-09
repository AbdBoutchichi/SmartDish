package com.springbootTemplate.univ.soa.request;

import com.springbootTemplate.univ.soa.response.DifficulteEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Requête pour créer une nouvelle recette")
public class RecetteCreateRequest {

    @Schema(description = "Titre de la recette", example = "Pâtes Carbonara", required = true)
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 200, message = "Le titre doit contenir entre 3 et 200 caractères")
    private String titre;

    @Schema(description = "Description de la recette", example = "Une délicieuse recette italienne traditionnelle")
    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    private String description;

    @Schema(description = "Temps total de préparation en minutes", example = "30", minimum = "1", maximum = "1440")
    @Min(value = 1, message = "Le temps total doit être supérieur à 0")
    @Max(value = 1440, message = "Le temps total ne peut pas dépasser 1440 minutes (24h)")
    private Integer tempsTotal;

    @Schema(description = "Nombre de calories", example = "500", minimum = "0", maximum = "10000")
    @Min(value = 0, message = "Les calories ne peuvent pas être négatives")
    @Max(value = 10000, message = "Les calories semblent excessives (max 10000)")
    private Integer kcal;

    @Schema(description = "URL de l'image de la recette", example = "https://example.com/carbonara.jpg")
    @Size(max = 500, message = "L'URL de l'image ne peut pas dépasser 500 caractères")
    private String imageUrl;

    @Schema(description = "Niveau de difficulté", example = "MOYEN", allowableValues = {"FACILE", "MOYEN", "DIFFICILE"})
    private DifficulteEnum difficulte;

    @Schema(description = "Catégorie de la recette", example = "PLAT_PRINCIPAL")
    private String categorie;

    @Schema(description = "Liste des ingrédients")
    private List<IngredientRequest> ingredients;

    @Schema(description = "Liste des étapes de préparation")
    private List<EtapeRequest> etapes;

    @Schema(description = "Tags associés à la recette", example = "[\"végétarien\", \"rapide\", \"italien\"]")
    private List<String> tags;
}

