package com.springbootTemplate.univ.soa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {

    private Long id;
    private Long userId;
    private String userEmail; // Email de l'utilisateur (pour affichage)
    private Long recetteId;
    private String recetteTitre; // Titre de la recette (pour affichage)
    private Integer note;
    private String commentaire;
    private LocalDateTime dateFeedback;

    // Constructeur pour la création (sans ID et sans date)
    public FeedbackDTO(Long userId, Long recetteId, Integer note, String commentaire) {
        this.userId = userId;
        this.recetteId = recetteId;
        this.note = note;
        this.commentaire = commentaire;
    }

    // Constructeur simplifié (les plus utilisés)
    public FeedbackDTO(Long id, Long userId, Long recetteId, Integer note, String commentaire, LocalDateTime dateFeedback) {
        this.id = id;
        this.userId = userId;
        this.recetteId = recetteId;
        this.note = note;
        this.commentaire = commentaire;
        this.dateFeedback = dateFeedback;
    }
}