package com.spingbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.dto.UtilisateurDTO;
import com.springbootTemplate.univ.soa.exception.ResourceNotFoundException;
import com.springbootTemplate.univ.soa.model.Aliment;
import com.springbootTemplate.univ.soa.model.Utilisateur;
import com.springbootTemplate.univ.soa.repository.AlimentRepository;
import com.springbootTemplate.univ.soa.repository.UtilisateurRepository;
import com.springbootTemplate.univ.soa.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UtilisateurServiceTest {

    private UtilisateurService utilisateurService;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private AlimentRepository alimentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        utilisateurService = new UtilisateurService();
        org.springframework.test.util.ReflectionTestUtils.setField(utilisateurService, "utilisateurRepository", utilisateurRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(utilisateurService, "alimentRepository", alimentRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(utilisateurService, "passwordEncoder", passwordEncoder);
    }

    @Test
    void save_HashesPasswordAndSaves() {
        Utilisateur u = new Utilisateur(); u.setMotDePasse("plain");
        when(passwordEncoder.encode("plain")).thenReturn("hashed");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));

        Utilisateur saved = utilisateurService.save(u);
        assertEquals("hashed", saved.getMotDePasse());
    }

    @Test
    void update_ThrowsWhenNotFound() {
        when(utilisateurRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> utilisateurService.update(5L, new Utilisateur()));
    }

    @Test
    void update_EncodesPasswordWhenProvided() {
        Utilisateur existing = new Utilisateur(); existing.setId(1L); existing.setMotDePasse("old");
        Utilisateur incoming = new Utilisateur(); incoming.setMotDePasse("newpass");
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newpass")).thenReturn("hashedNew");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));

        Utilisateur updated = utilisateurService.update(1L, incoming);
        assertEquals("hashedNew", updated.getMotDePasse());
    }

    @Test
    void saveFromDTO_ThrowsWhenAlimentMissing() {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setAlimentsExclusIds(new java.util.HashSet<>(List.of(9L)));
        when(alimentRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> utilisateurService.saveFromDTO(dto));
    }

    @Test
    void saveFromDTO_SetsAlimentsExclus() {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setAlimentsExclusIds(new java.util.HashSet<>(List.of(1L, 2L)));
        Aliment a1 = new Aliment(); a1.setId(1L);
        Aliment a2 = new Aliment(); a2.setId(2L);
        when(alimentRepository.findById(1L)).thenReturn(Optional.of(a1));
        when(alimentRepository.findById(2L)).thenReturn(Optional.of(a2));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));

        Utilisateur saved = utilisateurService.saveFromDTO(dto);
        assertEquals(2, saved.getAlimentsExclus().size());
    }

    @Test
    void updateFromDTO_ThrowsWhenAlimentMissing() {
        Utilisateur existing = new Utilisateur(); existing.setId(1L); existing.setAlimentsExclus(Collections.emptySet());
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(alimentRepository.findById(7L)).thenReturn(Optional.empty());

        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setAlimentsExclusIds(new java.util.HashSet<>(List.of(7L)));

        assertThrows(ResourceNotFoundException.class, () -> utilisateurService.updateFromDTO(1L, dto));
    }

    @Test
    void addAlimentExclu_ThrowsWhenUserMissing() {
        when(utilisateurRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> utilisateurService.addAlimentExclu(5L, 1L));
    }

    @Test
    void addAlimentExclu_ThrowsWhenAlimentMissing() {
        Utilisateur u = new Utilisateur(); u.setId(1L); u.setAlimentsExclus(Collections.emptySet());
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(u));
        when(alimentRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> utilisateurService.addAlimentExclu(1L, 2L));
    }

    @Test
    void removeAlimentExclu_RemovesWhenPresent() {
        Utilisateur u = new Utilisateur(); u.setId(1L);
        Aliment a = new Aliment(); a.setId(3L);
        u.setAlimentsExclus(new java.util.HashSet<>());
        u.getAlimentsExclus().add(a);
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(u));

        utilisateurService.removeAlimentExclu(1L, 3L);
        assertTrue(u.getAlimentsExclus().isEmpty());
        verify(utilisateurRepository).save(u);
    }

    @Test
    void deleteById_ThrowsWhenMissing() {
        when(utilisateurRepository.existsById(11L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> utilisateurService.deleteById(11L));
    }
}
