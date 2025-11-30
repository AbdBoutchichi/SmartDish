package com.spingbootTemplate.univ.soa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootTemplate.univ.soa.controller.FeedbackController;
import com.springbootTemplate.univ.soa.dto.FeedbackDTO;
import com.springbootTemplate.univ.soa.mapper.FeedbackMapper;
import com.springbootTemplate.univ.soa.model.Feedback;
import com.springbootTemplate.univ.soa.model.Recette;
import com.springbootTemplate.univ.soa.model.Utilisateur;
import com.springbootTemplate.univ.soa.service.FeedbackService;
import com.springbootTemplate.univ.soa.service.RecetteService;
import com.springbootTemplate.univ.soa.service.UtilisateurService;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private RecetteService recetteService;

    @Mock
    private FeedbackMapper feedbackMapper;

    private FeedbackDTO feedbackDTO;
    private Feedback feedback;
    private Utilisateur utilisateur;
    private Recette recette;

    @BeforeEach
    void setUp() {
        FeedbackController controller = new FeedbackController();
        ReflectionTestUtils.setField(controller, "feedbackService", feedbackService);
        ReflectionTestUtils.setField(controller, "utilisateurService", utilisateurService);
        ReflectionTestUtils.setField(controller, "recetteService", recetteService);
        ReflectionTestUtils.setField(controller, "feedbackMapper", feedbackMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        utilisateur = new Utilisateur();
        utilisateur.setId(1L);

        recette = new Recette();
        recette.setId(1L);

        feedback = new Feedback();
        feedback.setId(1L);
        feedback.setUtilisateur(utilisateur);
        feedback.setRecette(recette);
        feedback.setEvaluation(5);
        feedback.setCommentaire("Très bien");

        feedbackDTO = new FeedbackDTO();
        feedbackDTO.setId(1L);
        feedbackDTO.setUtilisateurId(1L);
        feedbackDTO.setRecetteId(1L);
        feedbackDTO.setEvaluation(5);
        feedbackDTO.setCommentaire("Très bien");
    }

    @Test
    void getAllFeedbacks_ReturnsList() throws Exception {
        List<Feedback> list = Arrays.asList(feedback);
        when(feedbackService.findAll()).thenReturn(list);
        when(feedbackMapper.toDTO(any(Feedback.class))).thenReturn(feedbackDTO);

        mockMvc.perform(get("/api/persistance/feedbacks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].evaluation").value(5));

        verify(feedbackService).findAll();
    }

    @Test
    void getFeedbackById_ReturnsFeedback_WhenFound() throws Exception {
        when(feedbackService.findById(1L)).thenReturn(Optional.of(feedback));
        when(feedbackMapper.toDTO(feedback)).thenReturn(feedbackDTO);

        mockMvc.perform(get("/api/persistance/feedbacks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.evaluation").value(5));

        verify(feedbackService).findById(1L);
    }

    @Test
    void getFeedbackById_ReturnsNotFound_WhenMissing() throws Exception {
        when(feedbackService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/persistance/feedbacks/1"))
                .andExpect(status().isNotFound());

        verify(feedbackService).findById(1L);
    }

    @Test
    void getFeedbacksByUtilisateur_ReturnsList() throws Exception {
        when(feedbackService.findByUtilisateurId(1L)).thenReturn(Arrays.asList(feedback));
        when(feedbackMapper.toDTO(any(Feedback.class))).thenReturn(feedbackDTO);

        mockMvc.perform(get("/api/persistance/feedbacks/utilisateur/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].utilisateurId").value(1));

        verify(feedbackService).findByUtilisateurId(1L);
    }

    @Test
    void getFeedbacksByRecette_ReturnsList() throws Exception {
        when(feedbackService.findByRecetteId(1L)).thenReturn(Arrays.asList(feedback));
        when(feedbackMapper.toDTO(any(Feedback.class))).thenReturn(feedbackDTO);

        mockMvc.perform(get("/api/persistance/feedbacks/recette/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recetteId").value(1));

        verify(feedbackService).findByRecetteId(1L);
    }

    @Test
    void createFeedback_ReturnsBadRequest_WhenUtilisateurIdMissing() throws Exception {
        feedbackDTO.setUtilisateurId(null);

        mockMvc.perform(post("/api/persistance/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("L'ID de l'utilisateur est obligatoire"));
    }

    @Test
    void createFeedback_ReturnsBadRequest_WhenRecetteIdMissing() throws Exception {
        feedbackDTO.setRecetteId(null);

        mockMvc.perform(post("/api/persistance/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("L'ID de la recette est obligatoire"));
    }

    @Test
    void createFeedback_ReturnsNotFound_WhenUtilisateurDoesNotExist() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/persistance/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Utilisateur non trouvé avec l'ID: 1"));

        verify(utilisateurService).findById(1L);
    }

    @Test
    void createFeedback_ReturnsNotFound_WhenRecetteDoesNotExist() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(recetteService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/persistance/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recette non trouvée avec l'ID: 1"));

        verify(recetteService).findById(1L);
    }

    @Test
    void createFeedback_ReturnsBadRequest_WhenEvaluationMissing() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(recetteService.findById(1L)).thenReturn(Optional.of(recette));
        feedbackDTO.setEvaluation(null);

        mockMvc.perform(post("/api/persistance/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("L'évaluation est obligatoire"));
    }

    @Test
    void createFeedback_ReturnsBadRequest_WhenEvaluationOutOfRange() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(recetteService.findById(1L)).thenReturn(Optional.of(recette));
        feedbackDTO.setEvaluation(6);

        mockMvc.perform(post("/api/persistance/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("L'évaluation doit être comprise entre 1 et 5 étoiles"));
    }

    @Test
    void createFeedback_ReturnsBadRequest_WhenCommentTooLong() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(recetteService.findById(1L)).thenReturn(Optional.of(recette));
        feedbackDTO.setCommentaire("A".repeat(1001));

        mockMvc.perform(post("/api/persistance/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le commentaire ne peut pas dépasser 1000 caractères"));
    }

    @Test
    void createFeedback_ReturnsConflict_WhenAlreadyRated() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(recetteService.findById(1L)).thenReturn(Optional.of(recette));

        Feedback existing = new Feedback();
        Recette r = new Recette();
        r.setId(1L);
        existing.setRecette(r);

        when(feedbackService.findByUtilisateurId(1L)).thenReturn(Arrays.asList(existing));

        mockMvc.perform(post("/api/persistance/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Vous avez déjà noté cette recette."));
    }

    @Test
    void createFeedback_ReturnsCreated_WhenValid() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(recetteService.findById(1L)).thenReturn(Optional.of(recette));
        when(feedbackService.findByUtilisateurId(1L)).thenReturn(Collections.emptyList());

        when(feedbackMapper.toEntity(any(FeedbackDTO.class))).thenReturn(feedback);
        when(feedbackService.save(any(Feedback.class), eq(1L), eq(1L))).thenReturn(feedback);
        when(feedbackMapper.toDTO(any(Feedback.class))).thenReturn(feedbackDTO);

        mockMvc.perform(post("/api/persistance/feedbacks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(feedbackService).save(any(Feedback.class), eq(1L), eq(1L));
    }

    @Test
    void updateFeedback_ReturnsNotFound_WhenMissing() throws Exception {
        when(feedbackService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/persistance/feedbacks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Feedback non trouvé avec l'ID: 1"));
    }

    @Test
    void updateFeedback_ReturnsBadRequest_WhenEvaluationMissing() throws Exception {
        when(feedbackService.findById(1L)).thenReturn(Optional.of(feedback));
        feedbackDTO.setEvaluation(null);

        mockMvc.perform(put("/api/persistance/feedbacks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("L'évaluation est obligatoire"));
    }

    @Test
    void updateFeedback_ReturnsBadRequest_WhenEvaluationOutOfRange() throws Exception {
        when(feedbackService.findById(1L)).thenReturn(Optional.of(feedback));
        feedbackDTO.setEvaluation(0);

        mockMvc.perform(put("/api/persistance/feedbacks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("L'évaluation doit être comprise entre 1 et 5 étoiles"));
    }

    @Test
    void updateFeedback_ReturnsBadRequest_WhenCommentTooLong() throws Exception {
        when(feedbackService.findById(1L)).thenReturn(Optional.of(feedback));
        feedbackDTO.setCommentaire("A".repeat(1001));

        mockMvc.perform(put("/api/persistance/feedbacks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le commentaire ne peut pas dépasser 1000 caractères"));
    }

    @Test
    void updateFeedback_ReturnsOk_WhenValid() throws Exception {
        when(feedbackService.findById(1L)).thenReturn(Optional.of(feedback));
        when(feedbackMapper.toEntity(any(FeedbackDTO.class))).thenReturn(feedback);
        when(feedbackService.update(eq(1L), any(Feedback.class))).thenReturn(feedback);
        when(feedbackMapper.toDTO(any(Feedback.class))).thenReturn(feedbackDTO);

        mockMvc.perform(put("/api/persistance/feedbacks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(feedbackService).update(eq(1L), any(Feedback.class));
    }

    @Test
    void deleteFeedback_ReturnsNotFound_WhenMissing() throws Exception {
        when(feedbackService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/persistance/feedbacks/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Feedback non trouvé avec l'ID: 1"));
    }

    @Test
    void deleteFeedback_ReturnsNoContent_WhenExists() throws Exception {
        when(feedbackService.findById(1L)).thenReturn(Optional.of(feedback));

        mockMvc.perform(delete("/api/persistance/feedbacks/1"))
                .andExpect(status().isNoContent());

        verify(feedbackService).deleteById(1L);
    }
}

