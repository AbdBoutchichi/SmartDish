package com.springbootTemplate.univ.soa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recette_id", nullable = false)
    private Long recetteId;

    @Column(name = "note", nullable = false)
    private Integer note; // 1 à 5 étoiles

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "date_feedback")
    private LocalDateTime dateFeedback;

    @PrePersist
    protected void onCreate() {
        if (dateFeedback == null) {
            dateFeedback = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dateFeedback = LocalDateTime.now();
    }
}