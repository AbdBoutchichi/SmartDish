package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.response.FichierRecetteResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FichierRecetteService {
    FichierRecetteResponse uploadImage(Long recetteId, MultipartFile file);
    FichierRecetteResponse uploadDocument(Long recetteId, MultipartFile file);
    List<FichierRecetteResponse> getFichiersByRecette(Long recetteId);
    List<FichierRecetteResponse> getImagesByRecette(Long recetteId);
    List<FichierRecetteResponse> getDocumentsByRecette(Long recetteId);
    ResponseEntity<Resource> downloadFichier(Long recetteId, Long fichierId);
    FichierRecetteResponse getFichierMetadata(Long recetteId, Long fichierId);
    void deleteFichier(Long recetteId, Long fichierId);
    void deleteAllFichiersByRecette(Long recetteId);
}

