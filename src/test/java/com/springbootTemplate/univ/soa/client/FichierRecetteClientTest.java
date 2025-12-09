package com.springbootTemplate.univ.soa.client;

import com.springbootTemplate.univ.soa.response.FichierRecetteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FichierRecetteClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FichierRecetteClient fichierRecetteClient;

    private FichierRecetteResponse fichierResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fichierRecetteClient, "persistanceServiceUrl", "http://localhost:8090");

        fichierResponse = new FichierRecetteResponse();
        fichierResponse.setId(1L);
        fichierResponse.setRecetteId(1L);
        fichierResponse.setNomOriginal("image.jpg");
        fichierResponse.setType("IMAGE");
        fichierResponse.setUrl("http://minio/bucket/image.jpg");
        fichierResponse.setDateCreation(LocalDateTime.now());
    }

    @Test
    @DisplayName("uploadImage - devrait uploader une image avec succès")
    void testUploadImage_Success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(file.getOriginalFilename()).thenReturn("image.jpg");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FichierRecetteResponse.class)
        )).thenReturn(new ResponseEntity<>(fichierResponse, HttpStatus.CREATED));

        FichierRecetteResponse result = fichierRecetteClient.uploadImage(1L, file);

        assertNotNull(result);
        assertEquals("image.jpg", result.getNomOriginal());
        assertEquals("IMAGE", result.getType());
    }

    @Test
    @DisplayName("uploadImage - devrait lancer une exception en cas d'erreur IO")
    void testUploadImage_IOException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Erreur lecture"));

        assertThrows(RuntimeException.class, () -> fichierRecetteClient.uploadImage(1L, file));
    }

    @Test
    @DisplayName("uploadDocument - devrait uploader un document avec succès")
    void testUploadDocument_Success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(file.getOriginalFilename()).thenReturn("doc.pdf");

        fichierResponse.setType("DOCUMENT");
        fichierResponse.setNomOriginal("doc.pdf");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(FichierRecetteResponse.class)
        )).thenReturn(new ResponseEntity<>(fichierResponse, HttpStatus.CREATED));

        FichierRecetteResponse result = fichierRecetteClient.uploadDocument(1L, file);

        assertNotNull(result);
        assertEquals("doc.pdf", result.getNomOriginal());
        assertEquals("DOCUMENT", result.getType());
    }

    @Test
    @DisplayName("getFichiersByRecette - devrait retourner la liste des fichiers")
    void testGetFichiersByRecette_Success() {
        List<FichierRecetteResponse> fichiers = Arrays.asList(fichierResponse);
        ResponseEntity<List<FichierRecetteResponse>> responseEntity = new ResponseEntity<>(fichiers, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        List<FichierRecetteResponse> result = fichierRecetteClient.getFichiersByRecette(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getImagesByRecette - devrait retourner les images")
    void testGetImagesByRecette_Success() {
        List<FichierRecetteResponse> images = Arrays.asList(fichierResponse);
        ResponseEntity<List<FichierRecetteResponse>> responseEntity = new ResponseEntity<>(images, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        List<FichierRecetteResponse> result = fichierRecetteClient.getImagesByRecette(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getDocumentsByRecette - devrait retourner les documents")
    void testGetDocumentsByRecette_Success() {
        List<FichierRecetteResponse> documents = Arrays.asList(fichierResponse);
        ResponseEntity<List<FichierRecetteResponse>> responseEntity = new ResponseEntity<>(documents, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        List<FichierRecetteResponse> result = fichierRecetteClient.getDocumentsByRecette(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("downloadFichier - devrait télécharger un fichier")
    void testDownloadFichier_Success() {
        Resource resource = mock(Resource.class);
        ResponseEntity<Resource> responseEntity = new ResponseEntity<>(resource, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(Resource.class)
        )).thenReturn(responseEntity);

        ResponseEntity<Resource> result = fichierRecetteClient.downloadFichier(1L, 1L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    @DisplayName("getFichierMetadata - devrait retourner les métadonnées")
    void testGetFichierMetadata_Success() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(FichierRecetteResponse.class)
        )).thenReturn(new ResponseEntity<>(fichierResponse, HttpStatus.OK));

        FichierRecetteResponse result = fichierRecetteClient.getFichierMetadata(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("deleteFichier - devrait supprimer un fichier")
    void testDeleteFichier_Success() {
        doNothing().when(restTemplate).delete(anyString());

        assertDoesNotThrow(() -> fichierRecetteClient.deleteFichier(1L, 1L));
        verify(restTemplate, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("deleteAllFichiersByRecette - devrait supprimer tous les fichiers")
    void testDeleteAllFichiersByRecette_Success() {
        doNothing().when(restTemplate).delete(anyString());

        assertDoesNotThrow(() -> fichierRecetteClient.deleteAllFichiersByRecette(1L));
        verify(restTemplate, times(1)).delete(anyString());
    }
}

