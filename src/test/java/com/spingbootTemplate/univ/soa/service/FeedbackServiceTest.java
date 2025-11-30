package com.spingbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.exception.ResourceNotFoundException;
import com.springbootTemplate.univ.soa.model.Feedback;
import com.springbootTemplate.univ.soa.model.Recette;
import com.springbootTemplate.univ.soa.model.Utilisateur;
import com.springbootTemplate.univ.soa.repository.FeedbackRepository;
import com.springbootTemplate.univ.soa.repository.RecetteRepository;
import com.springbootTemplate.univ.soa.repository.UtilisateurRepository;
import com.springbootTemplate.univ.soa.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {

    private FeedbackService feedbackService;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private RecetteRepository recetteRepository;

    @BeforeEach
    void setUp() {
        feedbackService = new FeedbackService();
        org.springframework.test.util.ReflectionTestUtils.setField(feedbackService, "feedbackRepository", feedbackRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(feedbackService, "utilisateurRepository", utilisateurRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(feedbackService, "recetteRepository", recetteRepository);
    }

    @Test
    void findAll_ReturnsList() {
        Feedback f = new Feedback(); f.setId(1L);
        when(feedbackRepository.findAll()).thenReturn(Arrays.asList(f));

        List<Feedback> res = feedbackService.findAll();
        assertEquals(1, res.size());
    }

    @Test
    void save_ThrowsWhenUtilisateurMissing() {
        Feedback f = new Feedback();
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> feedbackService.save(f, 1L, 1L));
    }

    @Test
    void save_ThrowsWhenRecetteMissing() {
        Feedback f = new Feedback();
        Utilisateur u = new Utilisateur(); u.setId(1L);
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(u));
        when(recetteRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> feedbackService.save(f, 1L, 2L));
    }

    @Test
    void save_AssignsUtilisateurAndRecetteAndSaves() {
        Feedback f = new Feedback();
        Utilisateur u = new Utilisateur(); u.setId(1L);
        Recette r = new Recette(); r.setId(2L);
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(u));
        when(recetteRepository.findById(2L)).thenReturn(Optional.of(r));
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(i -> i.getArgument(0));

        Feedback saved = feedbackService.save(f, 1L, 2L);
        assertEquals(u, saved.getUtilisateur());
        assertEquals(r, saved.getRecette());
    }

    @Test
    void update_ThrowsWhenMissing() {
        when(feedbackRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> feedbackService.update(5L, new Feedback()));
    }

    @Test
    void deleteById_ThrowsWhenMissing() {
        when(feedbackRepository.existsById(9L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> feedbackService.deleteById(9L));
    }

    @Test
    void deleteById_DeletesWhenExists() {
        when(feedbackRepository.existsById(1L)).thenReturn(true);
        doNothing().when(feedbackRepository).deleteById(1L);

        feedbackService.deleteById(1L);
        verify(feedbackRepository).deleteById(1L);
    }
}

