package com.spingbootTemplate.univ.soa.mapper;

import com.springbootTemplate.univ.soa.dto.RecetteDTO;
import com.springbootTemplate.univ.soa.mapper.RecetteMapper;
import com.springbootTemplate.univ.soa.model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class RecetteMapperTest {

    private final RecetteMapper mapper = new RecetteMapper();

    @Test
    void toDTO_ReturnsNull_WhenInputNull() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void toEntity_ReturnsNull_WhenInputNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toDTO_MapsIngredientsAndEtapes() {
        Recette r = new Recette();
        r.setId(1L);
        r.setTitre("Test");

        Ingredient ing = new Ingredient();
        ing.setId(10L);
        Aliment a = new Aliment(); a.setId(5L); a.setNom("Tomate");
        ing.setAliment(a);
        ing.setQuantite(100F);
        ing.setUnite(Ingredient.Unite.GRAMME);
        ing.setPrincipal(true);

        Etape e = new Etape();
        e.setId(20L);
        e.setOrdre(1);
        e.setTexte("Faire cuire");

        r.setIngredients(new ArrayList<>());
        r.getIngredients().add(ing);
        r.setEtapes(new ArrayList<>());
        r.getEtapes().add(e);

        RecetteDTO dto = mapper.toDTO(r);
        assertEquals(1L, dto.getId());
        assertEquals("Test", dto.getTitre());
        assertEquals(1, dto.getIngredients().size());
        assertEquals(1, dto.getEtapes().size());
        assertEquals(5L, dto.getIngredients().get(0).getAlimentId());
    }

    @Test
    void toEntity_MapsFields() {
        RecetteDTO dto = new RecetteDTO();
        dto.setId(2L);
        dto.setTitre("Titre");
        dto.setTempsTotal(30);

        Recette r = mapper.toEntity(dto);
        assertEquals(2L, r.getId());
        assertEquals("Titre", r.getTitre());
        assertEquals(30, r.getTempsTotal());
    }
}

