package com.spingbootTemplate.univ.soa.mapper;

import com.springbootTemplate.univ.soa.dto.FeedbackDTO;
import com.springbootTemplate.univ.soa.mapper.FeedbackMapper;
import com.springbootTemplate.univ.soa.model.Feedback;
import com.springbootTemplate.univ.soa.model.Recette;
import com.springbootTemplate.univ.soa.model.Utilisateur;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FeedbackMapperTest {

    private final FeedbackMapper mapper = new FeedbackMapper();

    @Test
    void toDTO_ReturnsNull_WhenInputNull() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void toEntity_ReturnsNull_WhenInputNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toDTO_MapsFieldsIncludingRelations() {
        Feedback f = new Feedback();
        f.setId(1L);
        f.setEvaluation(4);
        f.setCommentaire("Nice");
        f.setDateFeedback(LocalDateTime.now());
        f.setDateModification(LocalDateTime.now());

        Utilisateur u = new Utilisateur(); u.setId(10L);
        Recette r = new Recette(); r.setId(20L);
        f.setUtilisateur(u);
        f.setRecette(r);

        FeedbackDTO dto = mapper.toDTO(f);

        assertEquals(1L, dto.getId());
        assertEquals(4, dto.getEvaluation());
        assertEquals("Nice", dto.getCommentaire());
        assertEquals(10L, dto.getUtilisateurId());
        assertEquals(20L, dto.getRecetteId());
    }

    @Test
    void toEntity_MapsFieldsOnly() {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(2L);
        dto.setEvaluation(5);
        dto.setCommentaire("Great");

        Feedback f = mapper.toEntity(dto);
        assertEquals(2L, f.getId());
        assertEquals(5, f.getEvaluation());
        assertEquals("Great", f.getCommentaire());
    }
}

