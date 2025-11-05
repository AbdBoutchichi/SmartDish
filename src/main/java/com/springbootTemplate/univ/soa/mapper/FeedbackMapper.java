package com.springbootTemplate.univ.soa.mapper;

import com.springbootTemplate.univ.soa.dto.FeedbackDTO;
import com.springbootTemplate.univ.soa.model.Feedback;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {

    // Entity -> DTO
    public FeedbackDTO toDTO(Feedback feedback) {
        if (feedback == null) {
            return null;
        }

        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(feedback.getId());
        dto.setUserId(feedback.getUserId());
        dto.setRecetteId(feedback.getRecetteId());
        dto.setNote(feedback.getNote());
        dto.setCommentaire(feedback.getCommentaire());
        dto.setDateFeedback(feedback.getDateFeedback());

        return dto;
    }

    // Entity -> DTO enrichi (avec email et titre)
    public FeedbackDTO toDTOEnriched(Feedback feedback, String userEmail, String recetteTitre) {
        FeedbackDTO dto = toDTO(feedback);
        if (dto != null) {
            dto.setUserEmail(userEmail);
            dto.setRecetteTitre(recetteTitre);
        }
        return dto;
    }

    // DTO -> Entity
    public Feedback toEntity(FeedbackDTO dto) {
        if (dto == null) {
            return null;
        }

        Feedback feedback = new Feedback();
        feedback.setId(dto.getId());
        feedback.setUserId(dto.getUserId());
        feedback.setRecetteId(dto.getRecetteId());
        feedback.setNote(dto.getNote());
        feedback.setCommentaire(dto.getCommentaire());
        feedback.setDateFeedback(dto.getDateFeedback());

        return feedback;
    }
}