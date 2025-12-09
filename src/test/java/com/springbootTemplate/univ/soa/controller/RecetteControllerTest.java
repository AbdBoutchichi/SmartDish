package com.springbootTemplate.univ.soa.controller;

import com.springbootTemplate.univ.soa.request.RecetteCreateRequest;
import com.springbootTemplate.univ.soa.request.RecetteSearchRequest;
import com.springbootTemplate.univ.soa.response.RecetteResponse;
import com.springbootTemplate.univ.soa.service.RecetteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecetteController.class)
@DisplayName("Tests d'intégration - RecetteController")
class RecetteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecetteService recetteService;

    private RecetteResponse recetteResponse;

    @BeforeEach
    void setUp() {
        recetteResponse = new RecetteResponse();
        recetteResponse.setId(1L);
        recetteResponse.setTitre("Spaghetti Carbonara");
        recetteResponse.setDescription("Recette italienne");
        recetteResponse.setDifficulte("FACILE");
        recetteResponse.setCategorie("PLAT_PRINCIPAL");
        recetteResponse.setTempsTotal(35);
        recetteResponse.setKcal(450);
    }

    @Test
    @DisplayName("POST /api/recettes - devrait créer une recette")
    void testCreateRecette_Success() throws Exception {
        when(recetteService.createRecette(any(RecetteCreateRequest.class)))
                .thenReturn(recetteResponse);

        String requestBody = """
                {
                  "titre": "Spaghetti Carbonara",
                  "description": "Recette italienne",
                  "tempsPreparation": 15,
                  "tempsCuisson": 20,
                  "nombrePersonnes": 4,
                  "difficulte": "FACILE",
                  "categorie": "PLAT_PRINCIPAL"
                }
                """;

        mockMvc.perform(post("/api/recettes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titre").value("Spaghetti Carbonara"));
    }

    @Test
    @DisplayName("GET /api/recettes - devrait retourner toutes les recettes")
    void testGetAllRecettes_Success() throws Exception {
        List<RecetteResponse> recettes = Arrays.asList(recetteResponse);
        when(recetteService.getAllRecettes()).thenReturn(recettes);

        mockMvc.perform(get("/api/recettes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titre").value("Spaghetti Carbonara"));
    }

    @Test
    @DisplayName("GET /api/recettes/{id} - devrait retourner une recette par ID")
    void testGetRecetteById_Success() throws Exception {
        when(recetteService.getRecetteById(1L)).thenReturn(recetteResponse);

        mockMvc.perform(get("/api/recettes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titre").value("Spaghetti Carbonara"));
    }

    @Test
    @DisplayName("POST /api/recettes/search - devrait rechercher des recettes")
    void testSearchRecettes_Success() throws Exception {
        List<RecetteResponse> recettes = Arrays.asList(recetteResponse);
        when(recetteService.searchRecettes(any(RecetteSearchRequest.class)))
                .thenReturn(recettes);

        String searchBody = """
                {
                  "categorie": "PLAT_PRINCIPAL",
                  "difficulte": "FACILE"
                }
                """;

        mockMvc.perform(post("/api/recettes/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(searchBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categorie").value("PLAT_PRINCIPAL"));
    }

    @Test
    @DisplayName("GET /api/recettes/categorie/{categorie} - devrait filtrer par catégorie")
    void testGetRecettesByCategorie_Success() throws Exception {
        List<RecetteResponse> recettes = Arrays.asList(recetteResponse);
        when(recetteService.getRecettesByCategorie("PLAT_PRINCIPAL"))
                .thenReturn(recettes);

        mockMvc.perform(get("/api/recettes/categorie/PLAT_PRINCIPAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categorie").value("PLAT_PRINCIPAL"));
    }

    @Test
    @DisplayName("DELETE /api/recettes/{id} - devrait supprimer une recette")
    void testDeleteRecette_Success() throws Exception {
        mockMvc.perform(delete("/api/recettes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/recettes/{id}/exists - devrait vérifier l'existence")
    void testRecetteExists_True() throws Exception {
        when(recetteService.recetteExists(1L)).thenReturn(true);

        mockMvc.perform(get("/api/recettes/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/recettes/{id}/exists - devrait retourner false si inexistant")
    void testRecetteExists_False() throws Exception {
        when(recetteService.recetteExists(999L)).thenReturn(false);

        mockMvc.perform(get("/api/recettes/999/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}

