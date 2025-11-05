package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.exception.ResourceNotFoundException;
import com.springbootTemplate.univ.soa.model.Aliment;
import com.springbootTemplate.univ.soa.model.Ingredient;
import com.springbootTemplate.univ.soa.model.Recette;
import com.springbootTemplate.univ.soa.repository.AlimentRepository;
import com.springbootTemplate.univ.soa.repository.RecetteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RecetteService {

    @Autowired
    private RecetteRepository recetteRepository;

    @Autowired
    private AlimentRepository alimentRepository;

    public List<Recette> findAll() {
        return recetteRepository.findAll();
    }

    public Optional<Recette> findById(Long id) {
        return recetteRepository.findById(id);
    }

    public List<Recette> findByIngredients(List<Long> alimentIds) {
        return recetteRepository.findByAlimentIds(alimentIds);
    }

    public List<Recette> searchByTitre(String titre) {
        return recetteRepository.findByTitreContainingIgnoreCase(titre);
    }

    @Transactional
    public Recette save(Recette recette) {
        // IMPORTANT : Forcer l'ID à null pour la création
        recette.setId(null);

        // Si la recette a des ingrédients, on les traite
        if (recette.getIngredients() != null && !recette.getIngredients().isEmpty()) {
            for (Ingredient ingredient : recette.getIngredients()) {
                // Forcer l'ID de l'ingrédient à null aussi
                ingredient.setId(null);

                // Vérifier que l'aliment existe
                if (ingredient.getAliment() != null && ingredient.getAliment().getId() != null) {
                    Aliment aliment = alimentRepository.findById(ingredient.getAliment().getId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Aliment non trouvé avec l'ID: " + ingredient.getAliment().getId()
                            ));
                    ingredient.setAliment(aliment);
                }
                // Lier l'ingrédient à la recette
                ingredient.setRecette(recette);
            }
        }
        return recetteRepository.save(recette);
    }

    @Transactional
    public Recette update(Long id, Recette recette) {
        Recette existing = recetteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recette non trouvée avec l'ID: " + id));

        existing.setTitre(recette.getTitre());
        existing.setDescription(recette.getDescription());
        existing.setSteps(recette.getSteps());
        existing.setCookTime(recette.getCookTime());
        existing.setKcal(recette.getKcal());
        existing.setImageUrl(recette.getImageUrl());

        // Mise à jour des ingrédients
        if (recette.getIngredients() != null) {
            // Supprimer les anciens ingrédients
            existing.getIngredients().clear();

            // Ajouter les nouveaux
            for (Ingredient ingredient : recette.getIngredients()) {
                ingredient.setId(null); // Nouveau ingrédient

                if (ingredient.getAliment() != null && ingredient.getAliment().getId() != null) {
                    Aliment aliment = alimentRepository.findById(ingredient.getAliment().getId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Aliment non trouvé avec l'ID: " + ingredient.getAliment().getId()
                            ));
                    ingredient.setAliment(aliment);
                    ingredient.setRecette(existing);
                    existing.getIngredients().add(ingredient);
                }
            }
        }

        return recetteRepository.save(existing);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!recetteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recette non trouvée avec l'ID: " + id);
        }
        recetteRepository.deleteById(id);
    }
}