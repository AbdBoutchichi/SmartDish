package com.springbootTemplate.univ.soa.controller;

import com.springbootTemplate.univ.soa.dto.RecetteDTO;
import com.springbootTemplate.univ.soa.mapper.RecetteMapper;
import com.springbootTemplate.univ.soa.model.Recette;
import com.springbootTemplate.univ.soa.service.RecetteService;
import com.springbootTemplate.univ.soa.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/persistance/recettes")
@CrossOrigin(origins = "*") // À ajuster selon vos besoins
public class RecetteController {

    @Autowired
    private RecetteService recetteService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private RecetteMapper recetteMapper;

    /**
     * Récupérer toutes les recettes avec filtres optionnels
     * GET /api/persistance/recettes
     * GET /api/persistance/recettes?ingredientIds=1,2,3
     * GET /api/persistance/recettes?titre=pizza
     */
    @GetMapping
    public ResponseEntity<List<RecetteDTO>> getAllRecettes(
            @RequestParam(required = false) List<Long> ingredientIds,
            @RequestParam(required = false) String titre) {

        List<Recette> recettes;

        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            recettes = recetteService.findByIngredients(ingredientIds);
        } else if (titre != null && !titre.isEmpty()) {
            recettes = recetteService.searchByTitre(titre);
        } else {
            recettes = recetteService.findAll();
        }

        // Convertir en DTOs et enrichir avec les notes moyennes
        List<RecetteDTO> dtos = recettes.stream()
                .map(recette -> {
                    RecetteDTO dto = recetteMapper.toDTO(recette);
                    enrichWithFeedbackStats(dto);
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Récupérer une recette par ID
     * GET /api/persistance/recettes/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecetteDTO> getRecetteById(@PathVariable Long id) {
        return recetteService.findById(id)
                .map(recette -> {
                    RecetteDTO dto = recetteMapper.toDTO(recette);
                    enrichWithFeedbackStats(dto);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Vérifier si une recette existe
     * HEAD /api/persistance/recettes/1
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkRecetteExists(@PathVariable Long id) {
        if (recetteService.findById(id).isPresent()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Créer une nouvelle recette
     * POST /api/persistance/recettes
     */
    @PostMapping
    public ResponseEntity<RecetteDTO> createRecette(@RequestBody RecetteDTO dto) {
        Recette recette = recetteMapper.toEntity(dto);
        Recette saved = recetteService.save(recette);
        RecetteDTO responseDto = recetteMapper.toDTO(saved);
        enrichWithFeedbackStats(responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * Mettre à jour une recette existante
     * PUT /api/persistance/recettes/1
     */
    @PutMapping("/{id}")
    public ResponseEntity<RecetteDTO> updateRecette(
            @PathVariable Long id,
            @RequestBody RecetteDTO dto) {
        Recette recette = recetteMapper.toEntity(dto);
        Recette updated = recetteService.update(id, recette);
        RecetteDTO responseDto = recetteMapper.toDTO(updated);
        enrichWithFeedbackStats(responseDto);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Supprimer une recette
     * DELETE /api/persistance/recettes/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecette(@PathVariable Long id) {
        recetteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupérer les recettes triées par note moyenne
     * GET /api/persistance/recettes/top-rated?limit=10
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<RecetteDTO>> getTopRatedRecettes(
            @RequestParam(defaultValue = "10") int limit) {

        List<RecetteDTO> allRecettes = recetteService.findAll().stream()
                .map(recette -> {
                    RecetteDTO dto = recetteMapper.toDTO(recette);
                    enrichWithFeedbackStats(dto);
                    return dto;
                })
                .filter(dto -> dto.getAverageNote() != null && dto.getAverageNote() > 0)
                .sorted((a, b) -> Double.compare(b.getAverageNote(), a.getAverageNote()))
                .limit(limit)
                .collect(Collectors.toList());

        return ResponseEntity.ok(allRecettes);
    }

    /**
     * Recherche avancée de recettes
     * GET /api/persistance/recettes/search?titre=pizza&minNote=4&maxCookTime=30
     */
    @GetMapping("/search")
    public ResponseEntity<List<RecetteDTO>> searchRecettes(
            @RequestParam(required = false) String titre,
            @RequestParam(required = false) Double minNote,
            @RequestParam(required = false) Integer maxCookTime) {

        List<RecetteDTO> recettes = recetteService.findAll().stream()
                .map(recette -> {
                    RecetteDTO dto = recetteMapper.toDTO(recette);
                    enrichWithFeedbackStats(dto);
                    return dto;
                })
                .filter(dto -> {
                    // Filtre par titre
                    if (titre != null && !titre.isEmpty()) {
                        if (!dto.getTitre().toLowerCase().contains(titre.toLowerCase())) {
                            return false;
                        }
                    }

                    // Filtre par note minimale
                    if (minNote != null) {
                        if (dto.getAverageNote() == null || dto.getAverageNote() < minNote) {
                            return false;
                        }
                    }

                    // Filtre par temps de cuisson maximum
                    if (maxCookTime != null) {
                        if (dto.getCookTime() == null || dto.getCookTime() > maxCookTime) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(recettes);
    }

    /**
     * Méthode helper pour enrichir le DTO avec les statistiques de feedback
     */
    private void enrichWithFeedbackStats(RecetteDTO dto) {
        if (dto != null && dto.getId() != null) {
            Double avgNote = feedbackService.getAverageNoteForRecette(dto.getId());
            Long totalFeedbacks = feedbackService.countFeedbacksForRecette(dto.getId());

            dto.setAverageNote(avgNote);
            dto.setTotalFeedbacks(totalFeedbacks);
        }
    }
}