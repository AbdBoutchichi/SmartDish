package com.springbootTemplate.univ.soa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecetteDTO {

    private Long id;
    private String titre;
    private String description;
    private String steps;
    private Integer cookTime;
    private Integer kcal;
    private String imageUrl;
    private List<IngredientDTO> ingredients;
    private Double averageNote; // Note moyenne calculée
    private Long totalFeedbacks; // Nombre de feedbacks

    // Constructeur simplifié pour la création
    public RecetteDTO(String titre, String description, String steps, Integer cookTime, Integer kcal) {
        this.titre = titre;
        this.description = description;
        this.steps = steps;
        this.cookTime = cookTime;
        this.kcal = kcal;
    }

    // Classe interne pour les ingrédients
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientDTO {
        private Long id;
        private Long alimentId;
        private String alimentNom;
        private Integer quantite;
        private String unite;
        private String categorie; // PRINCIPAL ou SECONDAIRE

        // Constructeur pour la création (sans ID)
        public IngredientDTO(Long alimentId, Integer quantite, String unite, String categorie) {
            this.alimentId = alimentId;
            this.quantite = quantite;
            this.unite = unite;
            this.categorie = categorie;
        }
    }
}