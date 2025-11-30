package com.spingbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.dto.RecetteDTO;
import com.springbootTemplate.univ.soa.exception.ResourceNotFoundException;
import com.springbootTemplate.univ.soa.model.*;
import com.springbootTemplate.univ.soa.repository.AlimentRepository;
import com.springbootTemplate.univ.soa.repository.RecetteRepository;
import com.springbootTemplate.univ.soa.service.RecetteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecetteServiceTest {

    private RecetteService recetteService;

    @Mock
    private RecetteRepository recetteRepository;

    @Mock
    private AlimentRepository alimentRepository;

    @BeforeEach
    void setUp() {
        recetteService = new RecetteService();
        org.springframework.test.util.ReflectionTestUtils.setField(recetteService, "recetteRepository", recetteRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(recetteService, "alimentRepository", alimentRepository);
    }

    @Test
    void saveFromDTO_ThrowsWhenAlimentMissing() {
        RecetteDTO dto = new RecetteDTO();
        RecetteDTO.IngredientDTO ing = new RecetteDTO.IngredientDTO();
        ing.setAlimentId(5L);
        dto.setIngredients(Arrays.asList(ing));

        when(alimentRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recetteService.saveFromDTO(dto));
    }

    @Test
    void saveFromDTO_CreatesAndSaves() {
        RecetteDTO dto = new RecetteDTO();
        dto.setTitre("Test");
        RecetteDTO.IngredientDTO ing = new RecetteDTO.IngredientDTO();
        ing.setAlimentId(1L);
        ing.setQuantite(100F);
        ing.setUnite("GRAMME");
        dto.setIngredients(List.of(ing));

        Aliment aliment = new Aliment(); aliment.setId(1L);
        when(alimentRepository.findById(1L)).thenReturn(Optional.of(aliment));
        when(recetteRepository.save(any(Recette.class))).thenAnswer(i -> { Recette r = i.getArgument(0); r.setId(1L); return r; });

        Recette saved = recetteService.saveFromDTO(dto);
        assertEquals("Test", saved.getTitre());
        assertEquals(1, saved.getIngredients().size());
    }

    @Test
    void updateFromDTO_ThrowsWhenRecetteMissing() {
        when(recetteRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> recetteService.updateFromDTO(9L, new RecetteDTO()));
    }

    @Test
    void updateFromDTO_ThrowsWhenIngredientAlimentMissing() {
        RecetteDTO dto = new RecetteDTO();
        RecetteDTO.IngredientDTO ing = new RecetteDTO.IngredientDTO();
        ing.setAlimentId(7L);
        dto.setIngredients(List.of(ing));

        Recette existing = new Recette(); existing.setId(1L);
        when(recetteRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(alimentRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recetteService.updateFromDTO(1L, dto));
    }

    @Test
    void deleteById_ThrowsWhenMissing() {
        when(recetteRepository.existsById(4L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> recetteService.deleteById(4L));
    }

    @Test
    void deleteById_DeletesWhenExists() {
        when(recetteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(recetteRepository).deleteById(1L);

        recetteService.deleteById(1L);
        verify(recetteRepository).deleteById(1L);
    }
}

