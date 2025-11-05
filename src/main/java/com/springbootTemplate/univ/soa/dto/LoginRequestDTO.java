package com.springbootTemplate.univ.soa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pour la connexion d'un utilisateur")
public class LoginRequestDTO {

    @Schema(description = "Adresse email", example = "test@gmail.com", required = true)
    private String emailAddress;

    @Schema(description = "Mot de passe", example = "Password123!", required = true)
    private String password;
}