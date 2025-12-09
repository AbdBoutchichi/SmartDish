package com.springbootTemplate.univ.soa.controller;

import com.springbootTemplate.univ.soa.response.FichierRecetteResponse;
import com.springbootTemplate.univ.soa.service.FichierRecetteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recettes/{recetteId}/fichiers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Fichiers de Recettes", description = "Gestion des images et documents associés aux recettes")
public class FichierRecetteController {

    private final FichierRecetteService fichierRecetteService;

    @PostMapping("/images")
    @Operation(summary = "Upload une image pour une recette")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image uploadée avec succès"),
            @ApiResponse(responseCode = "400", description = "Fichier invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<?> uploadImage(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long recetteId,
            @Parameter(description = "Fichier image à uploader")
            @RequestParam("file") MultipartFile file) {

        log.info("POST /api/recettes/{}/fichiers/images - Upload image", recetteId);

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Le fichier est vide"));
            }

            FichierRecetteResponse fichierDTO = fichierRecetteService.uploadImage(recetteId, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(fichierDTO);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de l'upload: " + e.getMessage()));
        }
    }

    @PostMapping("/documents")
    @Operation(summary = "Upload un document pour une recette")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Document uploadé avec succès"),
            @ApiResponse(responseCode = "400", description = "Fichier invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<?> uploadDocument(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long recetteId,
            @Parameter(description = "Fichier document à uploader")
            @RequestParam("file") MultipartFile file) {

        log.info("POST /api/recettes/{}/fichiers/documents - Upload document", recetteId);

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Le fichier est vide"));
            }

            FichierRecetteResponse fichierDTO = fichierRecetteService.uploadDocument(recetteId, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(fichierDTO);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de l'upload: " + e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les fichiers d'une recette")
    @ApiResponse(responseCode = "200", description = "Liste des fichiers récupérée")
    public ResponseEntity<List<FichierRecetteResponse>> getAllFichiers(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long recetteId) {

        log.info("GET /api/recettes/{}/fichiers - Récupération des fichiers", recetteId);
        List<FichierRecetteResponse> fichiers = fichierRecetteService.getFichiersByRecette(recetteId);
        return ResponseEntity.ok(fichiers);
    }

    @GetMapping("/images")
    @Operation(summary = "Récupérer les images d'une recette")
    @ApiResponse(responseCode = "200", description = "Liste des images récupérée")
    public ResponseEntity<List<FichierRecetteResponse>> getImages(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long recetteId) {

        log.info("GET /api/recettes/{}/fichiers/images - Récupération des images", recetteId);
        List<FichierRecetteResponse> images = fichierRecetteService.getImagesByRecette(recetteId);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/documents")
    @Operation(summary = "Récupérer les documents d'une recette")
    @ApiResponse(responseCode = "200", description = "Liste des documents récupérée")
    public ResponseEntity<List<FichierRecetteResponse>> getDocuments(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long recetteId) {

        log.info("GET /api/recettes/{}/fichiers/documents - Récupération des documents", recetteId);
        List<FichierRecetteResponse> documents = fichierRecetteService.getDocumentsByRecette(recetteId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{fichierId}/download")
    @Operation(summary = "Télécharger un fichier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichier téléchargé"),
            @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    })
    public ResponseEntity<?> downloadFichier(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long recetteId,
            @Parameter(description = "ID du fichier", example = "1")
            @PathVariable Long fichierId) {

        log.info("GET /api/recettes/{}/fichiers/{}/download - Téléchargement", recetteId, fichierId);

        try {
            return fichierRecetteService.downloadFichier(recetteId, fichierId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors du téléchargement: " + e.getMessage()));
        }
    }

    @GetMapping("/{fichierId}")
    @Operation(summary = "Récupérer les métadonnées d'un fichier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Métadonnées récupérées"),
            @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    })
    public ResponseEntity<?> getFichierMetadata(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long recetteId,
            @Parameter(description = "ID du fichier", example = "1")
            @PathVariable Long fichierId) {

        log.info("GET /api/recettes/{}/fichiers/{} - Métadonnées", recetteId, fichierId);

        try {
            FichierRecetteResponse fichier = fichierRecetteService.getFichierMetadata(recetteId, fichierId);
            return ResponseEntity.ok(fichier);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Fichier non trouvé"));
        }
    }

    @DeleteMapping("/{fichierId}")
    @Operation(summary = "Supprimer un fichier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fichier supprimé"),
            @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    })
    public ResponseEntity<?> deleteFichier(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long recetteId,
            @Parameter(description = "ID du fichier", example = "1")
            @PathVariable Long fichierId) {

        log.info("DELETE /api/recettes/{}/fichiers/{} - Suppression", recetteId, fichierId);

        try {
            fichierRecetteService.deleteFichier(recetteId, fichierId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la suppression: " + e.getMessage()));
        }
    }

    @DeleteMapping
    @Operation(summary = "Supprimer tous les fichiers d'une recette")
    @ApiResponse(responseCode = "204", description = "Tous les fichiers supprimés")
    public ResponseEntity<?> deleteAllFichiers(
            @Parameter(description = "ID de la recette", example = "1")
            @PathVariable Long recetteId) {

        log.info("DELETE /api/recettes/{}/fichiers - Suppression de tous les fichiers", recetteId);

        try {
            fichierRecetteService.deleteAllFichiersByRecette(recetteId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la suppression: " + e.getMessage()));
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}

