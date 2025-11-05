package com.springbootTemplate.univ.soa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pour la création d'un utilisateur")
public class UtilisateurCreateDTO {

    @Schema(description = "Adresse email de l'utilisateur", example = "test@gmail.com", required = true)
    private String emailAddress;

    @Schema(description = "Mot de passe de l'utilisateur (sera hashé automatiquement)", example = "Password123!", required = true)
    private String password;

    @Schema(description = "Régime alimentaire", example = "VEGETARIEN",
            allowableValues = {"VEGETARIEN", "VEGETALIEN", "GLUTEN_FREE", "SANS_LACTOSE", "NONE"})
    private String regimeAlimentaire;

    @Schema(description = "Liste des IDs des aliments allergènes (optionnel)")
    private Set<Long> allergeneIds;
}