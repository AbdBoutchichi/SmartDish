package com.springbootTemplate.univ.soa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDTO {

    private Long id;
    private String emailAddress;
    private String password;
    private String regimeAlimentaire;
    private Set<Long> allergeneIds; // IDs des aliments allergènes
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructeur pour la création (sans ID)
    public UtilisateurDTO(String emailAddress, String password, String regimeAlimentaire) {
        this.emailAddress = emailAddress;
        this.password = password;
        this.regimeAlimentaire = regimeAlimentaire;
    }

    // Constructeur pour les réponses (sans password)
    public UtilisateurDTO(Long id, String emailAddress, String regimeAlimentaire, Set<Long> allergeneIds) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.regimeAlimentaire = regimeAlimentaire;
        this.allergeneIds = allergeneIds;
    }
}