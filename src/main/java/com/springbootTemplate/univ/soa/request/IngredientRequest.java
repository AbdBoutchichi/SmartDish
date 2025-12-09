package com.springbootTemplate.univ.soa.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Ingrédient d'une recette")
public class IngredientRequest {

    @Schema(description = "ID de l'aliment", example = "5", required = true)
    @NotNull(message = "L'ID de l'aliment est requis")
    private Long alimentId;

    @Schema(description = "Quantité", example = "200.0")
    @DecimalMin(value = "0.01", message = "La quantité doit être supérieure à 0")
    private Float quantite;

    @Schema(description = "Unité de mesure", example = "GRAMME", allowableValues = {"GRAMME", "KILOGRAMME", "LITRE", "MILLILITRE", "CUILLERE_A_SOUPE", "CUILLERE_A_CAFE", "SACHET", "UNITE"})
    private String unite;

    @Schema(description = "Indique si c'est un ingrédient principal", example = "true")
    private Boolean principal;
}
