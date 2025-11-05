package com.springbootTemplate.univ.soa.mapper;

import com.springbootTemplate.univ.soa.dto.UtilisateurDTO;
import com.springbootTemplate.univ.soa.model.Aliment;
import com.springbootTemplate.univ.soa.model.Utilisateur;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UtilisateurMapper {

    // Entity -> DTO (sans le mot de passe pour la sécurité)
    public UtilisateurDTO toDTO(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }

        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(utilisateur.getId());
        dto.setEmailAddress(utilisateur.getEmailAddress());
        dto.setRegimeAlimentaire(utilisateur.getRegimeAlimentaire());
        dto.setCreatedAt(utilisateur.getCreatedAt());
        dto.setUpdatedAt(utilisateur.getUpdatedAt());

        // Convertir les allergènes en IDs
        if (utilisateur.getAllergenes() != null) {
            dto.setAllergeneIds(
                    utilisateur.getAllergenes().stream()
                            .map(Aliment::getId)
                            .collect(Collectors.toSet())
            );
        }

        return dto;
    }

    // DTO -> Entity
    public Utilisateur toEntity(UtilisateurDTO dto) {
        if (dto == null) {
            return null;
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(dto.getId());
        utilisateur.setEmailAddress(dto.getEmailAddress());
        utilisateur.setPassword(dto.getPassword());
        utilisateur.setRegimeAlimentaire(dto.getRegimeAlimentaire());

        return utilisateur;
    }
}