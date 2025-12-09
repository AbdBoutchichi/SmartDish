package com.springbootTemplate.univ.soa.controller;


import com.springbootTemplate.univ.soa.request.RecetteCreateRequest;
import com.springbootTemplate.univ.soa.request.RecetteSearchRequest;
import com.springbootTemplate.univ.soa.request.RecetteUpdateRequest;
import com.springbootTemplate.univ.soa.response.RecetteResponse;
import com.springbootTemplate.univ.soa.response.RecetteStatsResponse;
import com.springbootTemplate.univ.soa.service.RecetteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/recettes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Recettes", description = "API de gestion des recettes")
public class RecetteController {

    private final RecetteService recetteService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle recette", description = "Crée une nouvelle recette avec tous ses détails")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recette créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<RecetteResponse> createRecette(
            @Valid @RequestBody RecetteCreateRequest request) {
        log.info("POST /api/recettes - Création d'une recette: {}", request.getTitre());
        RecetteResponse response = recetteService.createRecette(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les recettes", description = "Récupère la liste complète des recettes")
    @ApiResponse(responseCode = "200", description = "Liste des recettes récupérée avec succès")
    public ResponseEntity<List<RecetteResponse>> getAllRecettes() {
        log.info("GET /api/recettes - Récupération de toutes les recettes");
        List<RecetteResponse> recettes = recetteService.getAllRecettes();
        return ResponseEntity.ok(recettes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une recette par ID", description = "Récupère les détails d'une recette spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recette trouvée"),
            @ApiResponse(responseCode = "404", description = "Recette non trouvée")
    })
    public ResponseEntity<RecetteResponse> getRecetteById(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/recettes/{} - Récupération de la recette", id);
        RecetteResponse recette = recetteService.getRecetteById(id);
        return ResponseEntity.ok(recette);
    }

    @GetMapping("/{id}/async")
    @Operation(summary = "Récupérer une recette de manière asynchrone",
            description = "Récupère une recette avec traitement asynchrone pour optimiser les performances")
    public CompletableFuture<ResponseEntity<RecetteResponse>> getRecetteByIdAsync(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/recettes/{}/async - Récupération asynchrone de la recette", id);
        return recetteService.getRecetteByIdAsync(id)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/search")
    @Operation(summary = "Rechercher des recettes",
            description = "Recherche des recettes selon différents critères (catégorie, difficulté, ingrédients, etc.)")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche récupérés")
    public ResponseEntity<List<RecetteResponse>> searchRecettes(
            @Valid @RequestBody RecetteSearchRequest searchRequest) {
        log.info("POST /api/recettes/search - Recherche de recettes");
        List<RecetteResponse> recettes = recetteService.searchRecettes(searchRequest);
        return ResponseEntity.ok(recettes);
    }

    @GetMapping("/categorie/{categorie}")
    @Operation(summary = "Récupérer les recettes par catégorie",
            description = "Récupère toutes les recettes d'une catégorie spécifique")
    @ApiResponse(responseCode = "200", description = "Recettes de la catégorie récupérées")
    public ResponseEntity<List<RecetteResponse>> getRecettesByCategorie(
            @Parameter(description = "Nom de la catégorie", example = "PLAT_PRINCIPAL")
            @PathVariable String categorie) {
        log.info("GET /api/recettes/categorie/{} - Récupération par catégorie", categorie);
        List<RecetteResponse> recettes = recetteService.getRecettesByCategorie(categorie);
        return ResponseEntity.ok(recettes);
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Récupérer les statistiques d'une recette",
            description = "Récupère les statistiques détaillées (notes, vues, favoris) d'une recette")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées"),
            @ApiResponse(responseCode = "404", description = "Recette non trouvée")
    })
    public ResponseEntity<RecetteStatsResponse> getRecetteStats(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/recettes/{}/stats - Récupération des statistiques", id);
        RecetteStatsResponse stats = recetteService.getRecetteStats(id);
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une recette",
            description = "Met à jour les informations d'une recette existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recette mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Recette non trouvée"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<RecetteResponse> updateRecette(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RecetteUpdateRequest request) {
        log.info("PUT /api/recettes/{} - Mise à jour de la recette", id);
        RecetteResponse response = recetteService.updateRecette(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une recette", description = "Supprime une recette existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recette supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Recette non trouvée")
    })
    public ResponseEntity<Void> deleteRecette(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/recettes/{} - Suppression de la recette", id);
        recetteService.deleteRecette(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Vérifier l'existence d'une recette",
            description = "Vérifie si une recette existe dans le système")
    @ApiResponse(responseCode = "200", description = "Résultat de la vérification")
    public ResponseEntity<Boolean> recetteExists(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long id) {
        log.debug("GET /api/recettes/{}/exists - Vérification d'existence", id);
        boolean exists = recetteService.recetteExists(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/populaires")
    @Operation(summary = "Récupérer les recettes populaires",
            description = "Récupère les recettes les mieux notées et les plus vues")
    @ApiResponse(responseCode = "200", description = "Recettes populaires récupérées")
    public ResponseEntity<List<RecetteResponse>> getPopularRecettes(
            @Parameter(description = "Nombre de recettes à récupérer", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        log.info("GET /api/recettes/populaires?limit={} - Récupération des recettes populaires", limit);
        List<RecetteResponse> recettes = recetteService.getPopularRecettes(limit);
        return ResponseEntity.ok(recettes);
    }

    @GetMapping("/recentes")
    @Operation(summary = "Récupérer les recettes récentes",
            description = "Récupère les recettes les plus récemment ajoutées")
    @ApiResponse(responseCode = "200", description = "Recettes récentes récupérées")
    public ResponseEntity<List<RecetteResponse>> getRecentRecettes(
            @Parameter(description = "Nombre de recettes à récupérer", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        log.info("GET /api/recettes/recentes?limit={} - Récupération des recettes récentes", limit);
        List<RecetteResponse> recettes = recetteService.getRecentRecettes(limit);
        return ResponseEntity.ok(recettes);
    }
}
