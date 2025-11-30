package com.spingbootTemplate.univ.soa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootTemplate.univ.soa.controller.RecetteController;
import com.springbootTemplate.univ.soa.dto.RecetteDTO;
import com.springbootTemplate.univ.soa.mapper.RecetteMapper;
import com.springbootTemplate.univ.soa.model.Recette;
import com.springbootTemplate.univ.soa.service.RecetteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RecetteControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RecetteService recetteService;

    @Mock
    private RecetteMapper recetteMapper;

    private RecetteDTO dto;
    private Recette recette;

    @BeforeEach
    void setUp() {
        RecetteController controller = new RecetteController();
        ReflectionTestUtils.setField(controller, "recetteService", recetteService);
        ReflectionTestUtils.setField(controller, "recetteMapper", recetteMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        dto = new RecetteDTO();
        dto.setId(1L);
        dto.setTitre("Salade");
        dto.setTempsTotal(10);
        dto.setKcal(150);
        dto.setDifficulte(null);

        RecetteDTO.IngredientDTO ing = new RecetteDTO.IngredientDTO();
        ing.setAlimentId(1L);
        ing.setQuantite(100F);
        ing.setUnite("GRAMME");
        dto.setIngredients(Arrays.asList(ing));

        RecetteDTO.EtapeDTO et = new RecetteDTO.EtapeDTO();
        et.setOrdre(1);
        et.setTexte("Mélanger les ingrédients");
        et.setTemps(5);
        dto.setEtapes(Arrays.asList(et));

        recette = new Recette();
        recette.setId(1L);
    }

    @Test
    void getAllRecettes_ReturnsList() throws Exception {
        when(recetteService.findAll()).thenReturn(Arrays.asList(recette));
        when(recetteMapper.toDTO(any(Recette.class))).thenReturn(dto);

        mockMvc.perform(get("/api/persistance/recettes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titre").value("Salade"));

        verify(recetteService).findAll();
    }

    @Test
    void getRecetteById_ReturnsRecette_WhenFound() throws Exception {
        when(recetteService.findById(1L)).thenReturn(Optional.of(recette));
        when(recetteMapper.toDTO(recette)).thenReturn(dto);

        mockMvc.perform(get("/api/persistance/recettes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titre").value("Salade"));

        verify(recetteService).findById(1L);
    }

    @Test
    void getRecetteById_ReturnsNotFound_WhenMissing() throws Exception {
        when(recetteService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/persistance/recettes/1"))
                .andExpect(status().isNotFound());

        verify(recetteService).findById(1L);
    }

    @Test
    void createRecette_ReturnsBadRequest_WhenTitreMissing() throws Exception {
        dto.setTitre(null);

        mockMvc.perform(post("/api/persistance/recettes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le titre de la recette est obligatoire"));
    }

    @Test
    void createRecette_ReturnsBadRequest_WhenTitreTooShort() throws Exception {
        dto.setTitre("Ab");

        mockMvc.perform(post("/api/persistance/recettes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le titre doit contenir au moins 3 caractères"));
    }

    @Test
    void createRecette_ReturnsBadRequest_WhenTitreTooLong() throws Exception {
        dto.setTitre("A".repeat(201));

        mockMvc.perform(post("/api/persistance/recettes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le titre ne peut pas dépasser 200 caractères"));
    }

    @Test
    void createRecette_ReturnsBadRequest_WhenTempsTotalInvalid() throws Exception {
        dto.setTempsTotal(0);

        mockMvc.perform(post("/api/persistance/recettes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le temps total doit être supérieur à 0"));
    }

    @Test
    void createRecette_ReturnsBadRequest_WhenTempsTotalTooLarge() throws Exception {
        dto.setTempsTotal(1500);

        mockMvc.perform(post("/api/persistance/recettes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le temps total ne peut pas dépasser 1440 minutes (24h)"));
    }

    @Test
    void createRecette_ReturnsBadRequest_WhenKcalNegative() throws Exception {
        dto.setKcal(-10);

        mockMvc.perform(post("/api/persistance/recettes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Les calories ne peuvent pas être négatives"));
    }

    @Test
    void createRecette_ReturnsBadRequest_WhenKcalTooLarge() throws Exception {
        dto.setKcal(20000);

        mockMvc.perform(post("/api/persistance/recettes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Les calories semblent excessives (max 10000)"));
    }

    @Test
    void createRecette_ReturnsBadRequest_WhenIngredientAlimentIdMissing() throws Exception {
        RecetteDTO.IngredientDTO bad = new RecetteDTO.IngredientDTO();
        bad.setAlimentId(null);
        bad.setQuantite(10F);
        dto.setIngredients(Arrays.asList(bad));

        mockMvc.perform(post("/api/persistance/recettes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("L'ID de l'aliment est requis pour chaque ingrédient"));
    }

    @Test
    void createRecette_ReturnsBadRequest_WhenIngredientQuantiteInvalid() throws Exception {
        RecetteDTO.IngredientDTO bad = new RecetteDTO.IngredientDTO();
        bad.setAlimentId(1L);
        bad.setQuantite((float) 0);
        dto.setIngredients(Arrays.asList(bad));

        mockMvc.perform(post("/api/persistance/recettes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La quantité doit être supérieure à 0"));
    }

    @Test
    void createRecette_ReturnsBadRequest_WhenEtapeOrdreInvalid() throws Exception {
        RecetteDTO.EtapeDTO bad = new RecetteDTO.EtapeDTO();
        bad.setOrdre(0);
        bad.setTexte("Texte valide");
        dto.setEtapes(Arrays.asList(bad));

        mockMvc.perform(post("/api/persistance/recettes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("L'ordre de chaque étape doit être supérieur à 0"));
    }

    @Test
    void createRecette_ReturnsBadRequest_WhenEtapeTexteTooShort() throws Exception {
        RecetteDTO.EtapeDTO bad = new RecetteDTO.EtapeDTO();
        bad.setOrdre(1);
        bad.setTexte("abc");
        dto.setEtapes(Arrays.asList(bad));

        mockMvc.perform(post("/api/persistance/recettes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le texte de chaque étape doit contenir au moins 5 caractères"));
    }

    @Test
    void createRecette_ReturnsCreated_WhenValid() throws Exception {
        when(recetteService.saveFromDTO(any(RecetteDTO.class))).thenReturn(recette);
        when(recetteMapper.toDTO(recette)).thenReturn(dto);

        mockMvc.perform(post("/api/persistance/recettes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(recetteService).saveFromDTO(any(RecetteDTO.class));
    }

    @Test
    void updateRecette_ReturnsNotFound_WhenMissing() throws Exception {
        when(recetteService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/persistance/recettes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recette non trouvée avec l'ID: 1"));
    }

    @Test
    void updateRecette_ReturnsBadRequest_WhenTitreMissing() throws Exception {
        when(recetteService.findById(1L)).thenReturn(Optional.of(recette));
        dto.setTitre(null);

        mockMvc.perform(put("/api/persistance/recettes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le titre de la recette est obligatoire"));
    }

    @Test
    void updateRecette_ReturnsOk_WhenValid() throws Exception {
        when(recetteService.findById(1L)).thenReturn(Optional.of(recette));
        when(recetteMapper.toDTO(any(Recette.class))).thenReturn(dto);
        when(recetteService.updateFromDTO(eq(1L), any(RecetteDTO.class))).thenReturn(recette);

        mockMvc.perform(put("/api/persistance/recettes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(recetteService).updateFromDTO(eq(1L), any(RecetteDTO.class));
    }

    @Test
    void deleteRecette_ReturnsNotFound_WhenMissing() throws Exception {
        when(recetteService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/persistance/recettes/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recette non trouvée avec l'ID: 1"));
    }

    @Test
    void deleteRecette_ReturnsNoContent_WhenExists() throws Exception {
        when(recetteService.findById(1L)).thenReturn(Optional.of(recette));

        mockMvc.perform(delete("/api/persistance/recettes/1"))
                .andExpect(status().isNoContent());

        verify(recetteService).deleteById(1L);
    }
}

