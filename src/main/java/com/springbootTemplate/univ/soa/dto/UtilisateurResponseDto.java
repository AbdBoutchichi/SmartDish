package com.springbootTemplate.univ.soa.dto;

import com.springbootTemplate.univ.soa.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurResponseDto {

    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    private Role role;
    private Boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}