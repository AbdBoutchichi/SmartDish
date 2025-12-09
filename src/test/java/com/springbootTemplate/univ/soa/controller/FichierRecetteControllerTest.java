package com.springbootTemplate.univ.soa.controller;

import com.springbootTemplate.univ.soa.response.FichierRecetteResponse;
import com.springbootTemplate.univ.soa.service.FichierRecetteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FichierRecetteController.class)
class FichierRecetteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FichierRecetteService fichierRecetteService;

    private FichierRecetteResponse fichierResponse;

    @BeforeEach
    void setUp() {
        fichierResponse = new FichierRecetteResponse();
        fichierResponse.setId(1L);
        fichierResponse.setRecetteId(1L);
        fichierResponse.setNomOriginal("test.jpg");
        fichierResponse.setType("IMAGE");
        fichierResponse.setUrl("http://minio/bucket/test.jpg");
        fichierResponse.setDateCreation(LocalDateTime.now());
    }

    @Test
    @DisplayName("uploadImage - devrait uploader une image avec succès")
    void testUploadImage_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        when(fichierRecetteService.uploadImage(anyLong(), any())).thenReturn(fichierResponse);

        mockMvc.perform(multipart("/api/recettes/1/fichiers/images")
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomOriginal").value("test.jpg"))
                .andExpect(jsonPath("$.type").value("IMAGE"));
    }

    @Test
    @DisplayName("uploadImage - devrait retourner 400 si fichier vide")
    void testUploadImage_EmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/api/recettes/1/fichiers/images")
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("uploadDocument - devrait uploader un document avec succès")
    void testUploadDocument_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "test pdf content".getBytes()
        );

        fichierResponse.setType("DOCUMENT");
        fichierResponse.setNomOriginal("test.pdf");

        when(fichierRecetteService.uploadDocument(anyLong(), any())).thenReturn(fichierResponse);

        mockMvc.perform(multipart("/api/recettes/1/fichiers/documents")
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("DOCUMENT"));
    }

    @Test
    @DisplayName("getAllFichiers - devrait retourner tous les fichiers")
    void testGetAllFichiers_Success() throws Exception {
        List<FichierRecetteResponse> fichiers = Arrays.asList(fichierResponse);
        when(fichierRecetteService.getFichiersByRecette(anyLong())).thenReturn(fichiers);

        mockMvc.perform(get("/api/recettes/1/fichiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("getImages - devrait retourner les images")
    void testGetImages_Success() throws Exception {
        List<FichierRecetteResponse> images = Arrays.asList(fichierResponse);
        when(fichierRecetteService.getImagesByRecette(anyLong())).thenReturn(images);

        mockMvc.perform(get("/api/recettes/1/fichiers/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].type").value("IMAGE"));
    }

    @Test
    @DisplayName("getDocuments - devrait retourner les documents")
    void testGetDocuments_Success() throws Exception {
        fichierResponse.setType("DOCUMENT");
        List<FichierRecetteResponse> documents = Arrays.asList(fichierResponse);
        when(fichierRecetteService.getDocumentsByRecette(anyLong())).thenReturn(documents);

        mockMvc.perform(get("/api/recettes/1/fichiers/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].type").value("DOCUMENT"));
    }

    @Test
    @DisplayName("downloadFichier - devrait télécharger un fichier")
    void testDownloadFichier_Success() throws Exception {
        Resource resource = new ByteArrayResource("test content".getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.jpg\"");
        ResponseEntity<Resource> responseEntity = new ResponseEntity<>(resource, headers, HttpStatus.OK);

        when(fichierRecetteService.downloadFichier(anyLong(), anyLong())).thenReturn(responseEntity);

        mockMvc.perform(get("/api/recettes/1/fichiers/1/download"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getFichierMetadata - devrait retourner les métadonnées")
    void testGetFichierMetadata_Success() throws Exception {
        when(fichierRecetteService.getFichierMetadata(anyLong(), anyLong())).thenReturn(fichierResponse);

        mockMvc.perform(get("/api/recettes/1/fichiers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomOriginal").value("test.jpg"));
    }

    @Test
    @DisplayName("deleteFichier - devrait supprimer un fichier")
    void testDeleteFichier_Success() throws Exception {
        mockMvc.perform(delete("/api/recettes/1/fichiers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("deleteAllFichiers - devrait supprimer tous les fichiers")
    void testDeleteAllFichiers_Success() throws Exception {
        mockMvc.perform(delete("/api/recettes/1/fichiers"))
                .andExpect(status().isNoContent());
    }
}

