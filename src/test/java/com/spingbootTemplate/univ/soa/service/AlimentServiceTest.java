package com.spingbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.exception.ResourceNotFoundException;
import com.springbootTemplate.univ.soa.model.Aliment;
import com.springbootTemplate.univ.soa.repository.AlimentRepository;
import com.springbootTemplate.univ.soa.service.AlimentService;
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
public class AlimentServiceTest {

    private AlimentService alimentService;

    @Mock
    private AlimentRepository alimentRepository;

    @BeforeEach
    void setUp() {
        alimentService = new AlimentService();
        org.springframework.test.util.ReflectionTestUtils.setField(alimentService, "alimentRepository", alimentRepository);
    }

    @Test
    void findAll_ReturnsAllAliments() {
        Aliment a = new Aliment(); a.setId(1L); a.setNom("Tomate");
        when(alimentRepository.findAll()).thenReturn(Arrays.asList(a));

        List<Aliment> result = alimentService.findAll();

        assertEquals(1, result.size());
        assertEquals("Tomate", result.get(0).getNom());
        verify(alimentRepository).findAll();
    }

    @Test
    void findById_ReturnsOptionalWhenPresent() {
        Aliment a = new Aliment(); a.setId(1L);
        when(alimentRepository.findById(1L)).thenReturn(Optional.of(a));

        Optional<Aliment> res = alimentService.findById(1L);
        assertTrue(res.isPresent());
        assertEquals(1L, res.get().getId());
    }

    @Test
    void findById_ReturnsEmptyWhenMissing() {
        when(alimentRepository.findById(2L)).thenReturn(Optional.empty());
        assertTrue(alimentService.findById(2L).isEmpty());
    }

    @Test
    void findByNom_DelegatesToRepository() {
        Aliment a = new Aliment(); a.setNom("Pain");
        when(alimentRepository.findByNomIgnoreCase("Pain")).thenReturn(Optional.of(a));

        Optional<Aliment> res = alimentService.findByNom("Pain");
        assertTrue(res.isPresent());
        assertEquals("Pain", res.get().getNom());
    }

    @Test
    void save_SetsIdNullAndSaves() {
        Aliment a = new Aliment(); a.setId(5L); a.setNom("Riz");
        when(alimentRepository.save(any(Aliment.class))).thenAnswer(i -> i.getArgument(0));

        Aliment saved = alimentService.save(a);

        assertNull(saved.getId());
        assertEquals("Riz", saved.getNom());
        verify(alimentRepository).save(saved);
    }

    @Test
    void update_ThrowsWhenNotFound() {
        when(alimentRepository.findById(10L)).thenReturn(Optional.empty());
        Aliment toUpdate = new Aliment();
        assertThrows(ResourceNotFoundException.class, () -> alimentService.update(10L, toUpdate));
    }

    @Test
    void update_UpdatesFieldsAndSaves() {
        Aliment existing = new Aliment(); existing.setId(1L); existing.setNom("Old");
        Aliment incoming = new Aliment(); incoming.setNom("New"); incoming.setCategorie(Aliment.CategorieAliment.LEGUME);

        when(alimentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(alimentRepository.save(any(Aliment.class))).thenAnswer(i -> i.getArgument(0));

        Aliment updated = alimentService.update(1L, incoming);

        assertEquals("New", updated.getNom());
        assertEquals(Aliment.CategorieAliment.LEGUME, updated.getCategorie());
        verify(alimentRepository).save(existing);
    }

    @Test
    void deleteById_ThrowsWhenNotExists() {
        when(alimentRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> alimentService.deleteById(99L));
    }

    @Test
    void deleteById_DeletesWhenExists() {
        when(alimentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(alimentRepository).deleteById(1L);

        alimentService.deleteById(1L);

        verify(alimentRepository).deleteById(1L);
    }
}

