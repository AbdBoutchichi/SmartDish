package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.exception.ResourceNotFoundException;
import com.springbootTemplate.univ.soa.model.Feedback;
import com.springbootTemplate.univ.soa.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

    public Optional<Feedback> findById(Long id) {
        return feedbackRepository.findById(id);
    }

    public List<Feedback> findByUserId(Long userId) {
        return feedbackRepository.findByUserId(userId);
    }

    public List<Feedback> findByRecetteId(Long recetteId) {
        return feedbackRepository.findByRecetteId(recetteId);
    }

    public Optional<Feedback> findByUserIdAndRecetteId(Long userId, Long recetteId) {
        return feedbackRepository.findByUserIdAndRecetteId(userId, recetteId);
    }

    public Double getAverageNoteForRecette(Long recetteId) {
        return feedbackRepository.calculateAverageNoteForRecette(recetteId);
    }

    public Long countFeedbacksForRecette(Long recetteId) {
        return feedbackRepository.countByRecetteId(recetteId);
    }

    @Transactional
    public Feedback save(Feedback feedback) {
        // IMPORTANT : Forcer l'ID à null pour la création
        feedback.setId(null);

        if (feedback.getDateFeedback() == null) {
            feedback.setDateFeedback(LocalDateTime.now());
        }
        return feedbackRepository.save(feedback);
    }

    @Transactional
    public Feedback update(Long id, Feedback feedback) {
        Feedback existing = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback non trouvé avec l'ID: " + id));

        existing.setNote(feedback.getNote());
        existing.setCommentaire(feedback.getCommentaire());
        existing.setDateFeedback(LocalDateTime.now());

        return feedbackRepository.save(existing);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new ResourceNotFoundException("Feedback non trouvé avec l'ID: " + id);
        }
        feedbackRepository.deleteById(id);
    }
}