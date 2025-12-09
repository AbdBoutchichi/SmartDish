package com.springbootTemplate.univ.soa.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Niveau de difficulté d'une recette")
public enum DifficulteEnum {
    @Schema(description = "Recette facile")
    FACILE,

    @Schema(description = "Recette de difficulté moyenne")
    MOYEN,

    @Schema(description = "Recette difficile")
    DIFFICILE
}
