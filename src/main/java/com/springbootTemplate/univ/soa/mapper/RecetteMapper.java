package com.springbootTemplate.univ.soa.mapper;

import com.springbootTemplate.univ.soa.dto.RecetteDTO;
import com.springbootTemplate.univ.soa.model.Ingredient;
import com.springbootTemplate.univ.soa.model.Recette;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RecetteMapper {

    // Entity -> DTO
    public RecetteDTO toDTO(Recette recette) {
        if (recette == null) {
            return null;
        }

        RecetteDTO dto = new RecetteDTO();
        dto.setId(recette.getId());
        dto.setTitre(recette.getTitre());
        dto.setDescription(recette.getDescription());
        dto.setSteps(recette.getSteps());
        dto.setCookTime(recette.getCookTime());
        dto.setKcal(recette.getKcal());
        dto.setImageUrl(recette.getImageUrl());

        // Convertir les ingrédients
        if (recette.getIngredients() != null) {
            dto.setIngredients(
                    recette.getIngredients().stream()
                            .map(this::ingredientToDTO)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    // Ingredient Entity -> DTO
    private RecetteDTO.IngredientDTO ingredientToDTO(Ingredient ingredient) {
        RecetteDTO.IngredientDTO dto = new RecetteDTO.IngredientDTO();
        dto.setId(ingredient.getId());
        dto.setAlimentId(ingredient.getAliment().getId());
        dto.setAlimentNom(ingredient.getAliment().getNom());
        dto.setQuantite(ingredient.getQuantite());
        dto.setUnite(ingredient.getUnite());
        dto.setCategorie(ingredient.getCategorie());
        return dto;
    }

    // DTO -> Entity (simplifié, sans les ingrédients)
    public Recette toEntity(RecetteDTO dto) {
        if (dto == null) {
            return null;
        }

        Recette recette = new Recette();
        recette.setId(dto.getId());
        recette.setTitre(dto.getTitre());
        recette.setDescription(dto.getDescription());
        recette.setSteps(dto.getSteps());
        recette.setCookTime(dto.getCookTime());
        recette.setKcal(dto.getKcal());
        recette.setImageUrl(dto.getImageUrl());

        return recette;
    }
}