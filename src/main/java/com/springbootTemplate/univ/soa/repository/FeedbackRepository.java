package com.springbootTemplate.univ.soa.repository;

import com.springbootTemplate.univ.soa.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserId(Long userId);
    List<Feedback> findByRecetteId(Long recetteId);
    Optional<Feedback> findByUserIdAndRecetteId(Long userId, Long recetteId);

    // Calculer la note moyenne d'une recette
    @Query("SELECT AVG(f.note) FROM Feedback f WHERE f.recetteId = :recetteId")
    Double calculateAverageNoteForRecette(Long recetteId);

    // Compter les feedbacks pour une recette
    Long countByRecetteId(Long recetteId);
}