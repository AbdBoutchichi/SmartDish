package com.springbootTemplate.univ.soa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recette")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recette {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "steps", columnDefinition = "TEXT")
    private String steps;

    @Column(name = "cook_time")
    private Integer cookTime; // en minutes

    @Column(name = "kcal")
    private Integer kcal;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @OneToMany(mappedBy = "recette", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ingredient> ingredients = new ArrayList<>();

    // Méthode helper pour ajouter un ingrédient
    public void addIngredient(Aliment aliment, Integer quantite, String unite, String categorie) {
        Ingredient ingredient = new Ingredient();
        ingredient.setRecette(this);
        ingredient.setAliment(aliment);
        ingredient.setQuantite(quantite);
        ingredient.setUnite(unite);
        ingredient.setCategorie(categorie);
        ingredients.add(ingredient);
    }
}