package com.spingbootTemplate.univ.soa.mapper;

import com.springbootTemplate.univ.soa.dto.AlimentDTO;
import com.springbootTemplate.univ.soa.mapper.AlimentMapper;
import com.springbootTemplate.univ.soa.model.Aliment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AlimentMapperTest {

    private final AlimentMapper mapper = new AlimentMapper();

    @Test
    void toDTO_ReturnsNull_WhenInputNull() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void toEntity_ReturnsNull_WhenInputNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toDTO_MapsAllFields() {
        Aliment a = new Aliment();
        a.setId(1L);
        a.setNom("Tomate");
        a.setCategorie(Aliment.CategorieAliment.LEGUME);

        AlimentDTO dto = mapper.toDTO(a);

        assertEquals(1L, dto.getId());
        assertEquals("Tomate", dto.getNom());
        assertEquals(Aliment.CategorieAliment.LEGUME, dto.getCategorie());
    }

    @Test
    void toEntity_MapsAllFields() {
        AlimentDTO dto = new AlimentDTO();
        dto.setId(2L);
        dto.setNom("Riz");
        dto.setCategorie(Aliment.CategorieAliment.CEREALE);

        Aliment entity = mapper.toEntity(dto);

        assertEquals(2L, entity.getId());
        assertEquals("Riz", entity.getNom());
        assertEquals(Aliment.CategorieAliment.CEREALE, entity.getCategorie());
    }
}

