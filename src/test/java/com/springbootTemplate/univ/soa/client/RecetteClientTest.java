package com.springbootTemplate.univ.soa.client;

import com.springbootTemplate.univ.soa.request.RecetteCreateRequest;
import com.springbootTemplate.univ.soa.request.RecetteUpdateRequest;
import com.springbootTemplate.univ.soa.response.RecetteResponse;
import com.springbootTemplate.univ.soa.response.RecetteStatsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - RecetteClient")
class RecetteClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RecetteClient recetteClient;

    private RecetteResponse recetteResponse;
    private RecetteCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recetteClient, "recetteServiceUrl", "http://localhost:8090");

        recetteResponse = new RecetteResponse();
        recetteResponse.setId(1L);
        recetteResponse.setTitre("Test Recette");
        recetteResponse.setDifficulte("FACILE");
        recetteResponse.setCategorie("PLAT_PRINCIPAL");

        createRequest = new RecetteCreateRequest();
        createRequest.setTitre("Nouvelle Recette");
    }

    @Test
    @DisplayName("createRecette - devrait créer une recette avec succès")
    void testCreateRecette_Success() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(RecetteResponse.class)
        )).thenReturn(new ResponseEntity<>(recetteResponse, HttpStatus.CREATED));

        RecetteResponse result = recetteClient.createRecette(createRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(RecetteResponse.class)
        );
    }

    @Test
    @DisplayName("createRecette - devrait lancer une exception en cas d'erreur")
    void testCreateRecette_ThrowsException() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(RecetteResponse.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(RuntimeException.class, () -> {
            recetteClient.createRecette(createRequest);
        });
    }

    @Test
    @DisplayName("getAllRecettes - devrait retourner la liste des recettes")
    void testGetAllRecettes_Success() {
        List<RecetteResponse> recettes = Arrays.asList(recetteResponse);
        ResponseEntity<List<RecetteResponse>> responseEntity = new ResponseEntity<>(recettes, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        List<RecetteResponse> result = recetteClient.getAllRecettes();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    @DisplayName("getRecetteById - devrait retourner une recette par ID")
    void testGetRecetteById_Success() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(RecetteResponse.class)
        )).thenReturn(new ResponseEntity<>(recetteResponse, HttpStatus.OK));

        RecetteResponse result = recetteClient.getRecetteById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(RecetteResponse.class)
        );
    }

    @Test
    @DisplayName("getRecettesByCategorie - devrait retourner recettes par catégorie")
    void testGetRecettesByCategorie_Success() {
        List<RecetteResponse> recettes = Arrays.asList(recetteResponse);
        ResponseEntity<List<RecetteResponse>> responseEntity = new ResponseEntity<>(recettes, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        List<RecetteResponse> result = recetteClient.getRecettesByCategorie("PLAT_PRINCIPAL");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("updateRecette - devrait mettre à jour une recette")
    void testUpdateRecette_Success() {
        RecetteUpdateRequest updateRequest = new RecetteUpdateRequest();
        updateRequest.setTitre("Titre modifié");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(RecetteResponse.class)
        )).thenReturn(new ResponseEntity<>(recetteResponse, HttpStatus.OK));

        RecetteResponse result = recetteClient.updateRecette(1L, updateRequest);

        assertNotNull(result);
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(RecetteResponse.class)
        );
    }

    @Test
    @DisplayName("deleteRecette - devrait supprimer une recette")
    void testDeleteRecette_Success() {
        doNothing().when(restTemplate).delete(anyString());

        assertDoesNotThrow(() -> recetteClient.deleteRecette(1L));
        verify(restTemplate, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("recetteExists - devrait retourner true si recette existe")
    void testRecetteExists_True() {
        when(restTemplate.headForHeaders(anyString())).thenReturn(null);

        boolean result = recetteClient.recetteExists(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("recetteExists - devrait retourner false si not found")
    void testRecetteExists_False() {
        when(restTemplate.headForHeaders(anyString()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        boolean result = recetteClient.recetteExists(999L);

        assertFalse(result);
    }

    @Test
    @DisplayName("getRecetteStats - devrait retourner les statistiques")
    void testGetRecetteStats_Success() {
        RecetteStatsResponse stats = new RecetteStatsResponse();
        stats.setRecetteId(1L);
        stats.setNoteMoyenne(4.5);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(RecetteStatsResponse.class)
        )).thenReturn(new ResponseEntity<>(stats, HttpStatus.OK));

        RecetteStatsResponse result = recetteClient.getRecetteStats(1L);

        assertNotNull(result);
        assertEquals(4.5, result.getNoteMoyenne());
    }

    @Test
    @DisplayName("getAllRecettes - devrait lancer une exception en cas d'erreur")
    void testGetAllRecettes_ThrowsException() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Service unavailable"));

        assertThrows(RuntimeException.class, () -> recetteClient.getAllRecettes());
    }

    @Test
    @DisplayName("getRecetteById - devrait lancer une exception si not found")
    void testGetRecetteById_NotFound() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(RecetteResponse.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(RuntimeException.class, () -> recetteClient.getRecetteById(999L));
    }

    @Test
    @DisplayName("getRecettesByCategorie - devrait lancer une exception en cas d'erreur")
    void testGetRecettesByCategorie_ThrowsException() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Service error"));

        assertThrows(RuntimeException.class, () -> recetteClient.getRecettesByCategorie("PLAT_PRINCIPAL"));
    }

    @Test
    @DisplayName("updateRecette - devrait lancer une exception si not found")
    void testUpdateRecette_NotFound() {
        RecetteUpdateRequest updateRequest = new RecetteUpdateRequest();
        updateRequest.setTitre("Titre modifié");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(RecetteResponse.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(RuntimeException.class, () -> recetteClient.updateRecette(999L, updateRequest));
    }

    @Test
    @DisplayName("deleteRecette - devrait lancer une exception si not found")
    void testDeleteRecette_NotFound() {
        doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
                .when(restTemplate).delete(anyString());

        assertThrows(RuntimeException.class, () -> recetteClient.deleteRecette(999L));
    }

    @Test
    @DisplayName("recetteExists - devrait retourner false en cas d'erreur générique")
    void testRecetteExists_GenericError() {
        when(restTemplate.headForHeaders(anyString()))
                .thenThrow(new RuntimeException("Connection error"));

        boolean result = recetteClient.recetteExists(1L);

        assertFalse(result);
    }

    @Test
    @DisplayName("getRecetteStats - devrait lancer une exception en cas d'erreur")
    void testGetRecetteStats_ThrowsException() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                eq(RecetteStatsResponse.class)
        )).thenThrow(new RuntimeException("Stats unavailable"));

        assertThrows(RuntimeException.class, () -> recetteClient.getRecetteStats(1L));
    }
}
