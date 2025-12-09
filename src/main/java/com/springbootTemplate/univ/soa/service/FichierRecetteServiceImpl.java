package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.client.FichierRecetteClient;
import com.springbootTemplate.univ.soa.response.FichierRecetteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FichierRecetteServiceImpl implements FichierRecetteService {

    private final FichierRecetteClient fichierRecetteClient;

    @Override
    public FichierRecetteResponse uploadImage(Long recetteId, MultipartFile file) {
        log.info("Upload image pour recette {}", recetteId);
        try {
            return fichierRecetteClient.uploadImage(recetteId, file);
        } catch (Exception e) {
            log.error("Erreur lors de l'upload de l'image: {}", e.getMessage());
            throw new RuntimeException("Impossible d'uploader l'image: " + e.getMessage(), e);
        }
    }

    @Override
    public FichierRecetteResponse uploadDocument(Long recetteId, MultipartFile file) {
        log.info("Upload document pour recette {}", recetteId);
        try {
            return fichierRecetteClient.uploadDocument(recetteId, file);
        } catch (Exception e) {
            log.error("Erreur lors de l'upload du document: {}", e.getMessage());
            throw new RuntimeException("Impossible d'uploader le document: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FichierRecetteResponse> getFichiersByRecette(Long recetteId) {
        log.info("Récupération des fichiers de la recette {}", recetteId);
        try {
            return fichierRecetteClient.getFichiersByRecette(recetteId);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des fichiers: {}", e.getMessage());
            throw new RuntimeException("Impossible de récupérer les fichiers: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FichierRecetteResponse> getImagesByRecette(Long recetteId) {
        log.info("Récupération des images de la recette {}", recetteId);
        try {
            return fichierRecetteClient.getImagesByRecette(recetteId);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des images: {}", e.getMessage());
            throw new RuntimeException("Impossible de récupérer les images: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FichierRecetteResponse> getDocumentsByRecette(Long recetteId) {
        log.info("Récupération des documents de la recette {}", recetteId);
        try {
            return fichierRecetteClient.getDocumentsByRecette(recetteId);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des documents: {}", e.getMessage());
            throw new RuntimeException("Impossible de récupérer les documents: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseEntity<Resource> downloadFichier(Long recetteId, Long fichierId) {
        log.info("Téléchargement du fichier {} de la recette {}", fichierId, recetteId);
        try {
            return fichierRecetteClient.downloadFichier(recetteId, fichierId);
        } catch (Exception e) {
            log.error("Erreur lors du téléchargement du fichier: {}", e.getMessage());
            throw new RuntimeException("Impossible de télécharger le fichier: " + e.getMessage(), e);
        }
    }

    @Override
    public FichierRecetteResponse getFichierMetadata(Long recetteId, Long fichierId) {
        log.info("Récupération des métadonnées du fichier {} de la recette {}", fichierId, recetteId);
        try {
            return fichierRecetteClient.getFichierMetadata(recetteId, fichierId);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des métadonnées: {}", e.getMessage());
            throw new RuntimeException("Impossible de récupérer les métadonnées: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFichier(Long recetteId, Long fichierId) {
        log.info("Suppression du fichier {} de la recette {}", fichierId, recetteId);
        try {
            fichierRecetteClient.deleteFichier(recetteId, fichierId);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du fichier: {}", e.getMessage());
            throw new RuntimeException("Impossible de supprimer le fichier: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAllFichiersByRecette(Long recetteId) {
        log.info("Suppression de tous les fichiers de la recette {}", recetteId);
        try {
            fichierRecetteClient.deleteAllFichiersByRecette(recetteId);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression des fichiers: {}", e.getMessage());
            throw new RuntimeException("Impossible de supprimer les fichiers: " + e.getMessage(), e);
        }
    }
}

