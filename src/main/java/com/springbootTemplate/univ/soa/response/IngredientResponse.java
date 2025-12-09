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
@Schema(description = "Ingrédient d'une recette")
public class IngredientResponse {

    @Schema(description = "ID de l'ingrédient", example = "1")
    private Long id;

    @Schema(description = "ID de l'aliment", example = "5")
    private Long alimentId;

    @Schema(description = "Nom de l'aliment", example = "Spaghetti")
    private String alimentNom;

    @Schema(description = "Quantité", example = "200.0")
    private Float quantite;

    @Schema(description = "Unité de mesure", example = "GRAMME")
    private String unite;

    @Schema(description = "Ingrédient principal", example = "true")
    private Boolean principal;
}
