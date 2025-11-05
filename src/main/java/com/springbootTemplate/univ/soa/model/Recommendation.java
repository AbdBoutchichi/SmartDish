package com.springbootTemplate.univ.soa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recette_id", nullable = false)
    private Long recetteId;

    @Column(name = "score")
    private Double score; // Score de l'algorithme RL

    @Column(name = "date_recommendation")
    private LocalDateTime dateRecommendation;

    @PrePersist
    protected void onCreate() {
        if (dateRecommendation == null) {
            dateRecommendation = LocalDateTime.now();
        }
    }
}