package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.client.FichierRecetteClient;
import com.springbootTemplate.univ.soa.response.FichierRecetteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FichierRecetteServiceImplTest {

    @Mock
    private FichierRecetteClient fichierRecetteClient;

    @InjectMocks
    private FichierRecetteServiceImpl fichierRecetteService;

    private FichierRecetteResponse fichierResponse;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        fichierResponse = new FichierRecetteResponse();
        fichierResponse.setId(1L);
        fichierResponse.setRecetteId(1L);
        fichierResponse.setNomOriginal("test.jpg");
        fichierResponse.setType("IMAGE");
        fichierResponse.setUrl("http://minio/bucket/test.jpg");
        fichierResponse.setDateCreation(LocalDateTime.now());

        file = mock(MultipartFile.class);
    }

    @Test
    @DisplayName("uploadImage - devrait uploader une image avec succès")
    void testUploadImage_Success() {
        when(fichierRecetteClient.uploadImage(anyLong(), any(MultipartFile.class)))
                .thenReturn(fichierResponse);

        FichierRecetteResponse result = fichierRecetteService.uploadImage(1L, file);

        assertNotNull(result);
        assertEquals("test.jpg", result.getNomOriginal());
        verify(fichierRecetteClient, times(1)).uploadImage(1L, file);
    }

    @Test
    @DisplayName("uploadImage - devrait lancer une exception en cas d'erreur")
    void testUploadImage_ThrowsException() {
        when(fichierRecetteClient.uploadImage(anyLong(), any(MultipartFile.class)))
                .thenThrow(new RuntimeException("Erreur upload"));

        assertThrows(RuntimeException.class, () -> fichierRecetteService.uploadImage(1L, file));
    }

    @Test
    @DisplayName("uploadDocument - devrait uploader un document avec succès")
    void testUploadDocument_Success() {
        fichierResponse.setType("DOCUMENT");
        when(fichierRecetteClient.uploadDocument(anyLong(), any(MultipartFile.class)))
                .thenReturn(fichierResponse);

        FichierRecetteResponse result = fichierRecetteService.uploadDocument(1L, file);

        assertNotNull(result);
        assertEquals("DOCUMENT", result.getType());
        verify(fichierRecetteClient, times(1)).uploadDocument(1L, file);
    }

    @Test
    @DisplayName("uploadDocument - devrait lancer une exception en cas d'erreur")
    void testUploadDocument_ThrowsException() {
        when(fichierRecetteClient.uploadDocument(anyLong(), any(MultipartFile.class)))
                .thenThrow(new RuntimeException("Erreur upload"));

        assertThrows(RuntimeException.class, () -> fichierRecetteService.uploadDocument(1L, file));
    }

    @Test
    @DisplayName("getFichiersByRecette - devrait retourner la liste des fichiers")
    void testGetFichiersByRecette_Success() {
        List<FichierRecetteResponse> fichiers = Arrays.asList(fichierResponse);
        when(fichierRecetteClient.getFichiersByRecette(anyLong())).thenReturn(fichiers);

        List<FichierRecetteResponse> result = fichierRecetteService.getFichiersByRecette(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(fichierRecetteClient, times(1)).getFichiersByRecette(1L);
    }

    @Test
    @DisplayName("getImagesByRecette - devrait retourner les images")
    void testGetImagesByRecette_Success() {
        List<FichierRecetteResponse> images = Arrays.asList(fichierResponse);
        when(fichierRecetteClient.getImagesByRecette(anyLong())).thenReturn(images);

        List<FichierRecetteResponse> result = fichierRecetteService.getImagesByRecette(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(fichierRecetteClient, times(1)).getImagesByRecette(1L);
    }

    @Test
    @DisplayName("getDocumentsByRecette - devrait retourner les documents")
    void testGetDocumentsByRecette_Success() {
        List<FichierRecetteResponse> documents = Arrays.asList(fichierResponse);
        when(fichierRecetteClient.getDocumentsByRecette(anyLong())).thenReturn(documents);

        List<FichierRecetteResponse> result = fichierRecetteService.getDocumentsByRecette(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(fichierRecetteClient, times(1)).getDocumentsByRecette(1L);
    }

    @Test
    @DisplayName("downloadFichier - devrait télécharger un fichier")
    void testDownloadFichier_Success() {
        Resource resource = mock(Resource.class);
        ResponseEntity<Resource> responseEntity = new ResponseEntity<>(resource, HttpStatus.OK);
        when(fichierRecetteClient.downloadFichier(anyLong(), anyLong())).thenReturn(responseEntity);

        ResponseEntity<Resource> result = fichierRecetteService.downloadFichier(1L, 1L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(fichierRecetteClient, times(1)).downloadFichier(1L, 1L);
    }

    @Test
    @DisplayName("getFichierMetadata - devrait retourner les métadonnées")
    void testGetFichierMetadata_Success() {
        when(fichierRecetteClient.getFichierMetadata(anyLong(), anyLong())).thenReturn(fichierResponse);

        FichierRecetteResponse result = fichierRecetteService.getFichierMetadata(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(fichierRecetteClient, times(1)).getFichierMetadata(1L, 1L);
    }

    @Test
    @DisplayName("deleteFichier - devrait supprimer un fichier")
    void testDeleteFichier_Success() {
        doNothing().when(fichierRecetteClient).deleteFichier(anyLong(), anyLong());

        assertDoesNotThrow(() -> fichierRecetteService.deleteFichier(1L, 1L));
        verify(fichierRecetteClient, times(1)).deleteFichier(1L, 1L);
    }

    @Test
    @DisplayName("deleteAllFichiersByRecette - devrait supprimer tous les fichiers")
    void testDeleteAllFichiersByRecette_Success() {
        doNothing().when(fichierRecetteClient).deleteAllFichiersByRecette(anyLong());

        assertDoesNotThrow(() -> fichierRecetteService.deleteAllFichiersByRecette(1L));
        verify(fichierRecetteClient, times(1)).deleteAllFichiersByRecette(1L);
    }
}

