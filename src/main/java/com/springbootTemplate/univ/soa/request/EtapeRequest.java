package com.springbootTemplate.univ.soa.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Étape de préparation")
public class EtapeRequest {

    @Schema(description = "Ordre de l'étape", example = "1", required = true)
    @NotNull(message = "L'ordre est requis")
    @Min(value = 1, message = "L'ordre doit être supérieur à 0")
    private Integer ordre;

    @Schema(description = "Temps estimé pour cette étape en minutes", example = "10")
    @Min(value = 0, message = "Le temps ne peut pas être négatif")
    private Integer temps;

    @Schema(description = "Description de l'étape", example = "Faire cuire les pâtes dans l'eau bouillante salée", required = true)
    @NotBlank(message = "Le texte de l'étape est obligatoire")
    @Size(min = 5, max = 1000, message = "Le texte doit contenir entre 5 et 1000 caractères")
    private String texte;
}
