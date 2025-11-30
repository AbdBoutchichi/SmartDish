package com.spingbootTemplate.univ.soa.mapper;

import com.springbootTemplate.univ.soa.dto.UtilisateurDTO;
import com.springbootTemplate.univ.soa.mapper.UtilisateurMapper;
import com.springbootTemplate.univ.soa.model.Aliment;
import com.springbootTemplate.univ.soa.model.Utilisateur;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class UtilisateurMapperTest {

    private final UtilisateurMapper mapper = new UtilisateurMapper();

    @Test
    void toDTO_ReturnsNull_WhenInputNull() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void toEntity_ReturnsNull_WhenInputNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toDTO_MapsFieldsAndAlimentsExclus() {
        Utilisateur u = new Utilisateur();
        u.setId(1L);
        u.setEmail("user@test.com");
        u.setNom("Dupont");

        Aliment a = new Aliment(); a.setId(5L);
        u.setAlimentsExclus(new HashSet<>());
        u.getAlimentsExclus().add(a);

        UtilisateurDTO dto = mapper.toDTO(u);
        assertEquals(1L, dto.getId());
        assertEquals("user@test.com", dto.getEmail());
        assertTrue(dto.getAlimentsExclusIds().contains(5L));
    }

    @Test
    void toEntity_MapsFieldsButNotAlimentsExclus() {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(2L);
        dto.setEmail("hello@test.com");
        dto.setMotDePasse("pwd");
        dto.setNom("Nom");
        dto.setPrenom("Prenom");

        Utilisateur u = mapper.toEntity(dto);
        assertEquals(2L, u.getId());
        assertEquals("hello@test.com", u.getEmail());
        assertEquals("pwd", u.getMotDePasse());
    }
}