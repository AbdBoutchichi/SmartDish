package com.springbootTemplate.univ.soa.controller;

import com.springbootTemplate.univ.soa.dto.FeedbackDTO;
import com.springbootTemplate.univ.soa.mapper.FeedbackMapper;
import com.springbootTemplate.univ.soa.model.Feedback;
import com.springbootTemplate.univ.soa.model.Recette;
import com.springbootTemplate.univ.soa.model.Utilisateur;
import com.springbootTemplate.univ.soa.service.FeedbackService;
import com.springbootTemplate.univ.soa.service.RecetteService;
import com.springbootTemplate.univ.soa.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/persistance/feedbacks")
@CrossOrigin(origins = "*") // À ajuster selon vos besoins
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private RecetteService recetteService;

    @Autowired
    private FeedbackMapper feedbackMapper;

    /**
     * Récupérer tous les feedbacks
     * GET /api/persistance/feedbacks
     */
    @GetMapping
    public ResponseEntity<List<FeedbackDTO>> getAllFeedbacks() {
        List<FeedbackDTO> dtos = feedbackService.findAll().stream()
                .map(this::enrichFeedbackDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Récupérer un feedback par ID
     * GET /api/persistance/feedbacks/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDTO> getFeedbackById(@PathVariable Long id) {
        return feedbackService.findById(id)
                .map(this::enrichFeedbackDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Vérifier si un feedback existe pour un utilisateur
     * HEAD /api/persistance/feedbacks/utilisateur/1
     */
    @RequestMapping(value = "/utilisateur/{userId}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkUserHasFeedbacks(@PathVariable Long userId) {
        List<Feedback> feedbacks = feedbackService.findByUserId(userId);
        if (!feedbacks.isEmpty()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Récupérer tous les feedbacks d'un utilisateur
     * GET /api/persistance/feedbacks/utilisateur/1
     */
    @GetMapping("/utilisateur/{userId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacksByUserId(@PathVariable Long userId) {
        List<FeedbackDTO> dtos = feedbackService.findByUserId(userId).stream()
                .map(this::enrichFeedbackDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Récupérer tous les feedbacks d'une recette
     * GET /api/persistance/feedbacks/recette/1
     */
    @GetMapping("/recette/{recetteId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacksByRecetteId(@PathVariable Long recetteId) {
        List<FeedbackDTO> dtos = feedbackService.findByRecetteId(recetteId).stream()
                .map(this::enrichFeedbackDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Récupérer les statistiques d'une recette (note moyenne et nombre de feedbacks)
     * GET /api/persistance/feedbacks/recette/1/stats
     */
    @GetMapping("/recette/{recetteId}/stats")
    public ResponseEntity<Map<String, Object>> getRecetteStats(@PathVariable Long recetteId) {
        Map<String, Object> stats = new HashMap<>();

        Double averageNote = feedbackService.getAverageNoteForRecette(recetteId);
        Long totalFeedbacks = feedbackService.countFeedbacksForRecette(recetteId);

        stats.put("recetteId", recetteId);
        stats.put("averageNote", averageNote != null ? averageNote : 0.0);
        stats.put("totalFeedbacks", totalFeedbacks);

        // Distribution des notes
        List<Feedback> feedbacks = feedbackService.findByRecetteId(recetteId);
        Map<Integer, Long> noteDistribution = feedbacks.stream()
                .collect(Collectors.groupingBy(Feedback::getNote, Collectors.counting()));

        stats.put("noteDistribution", noteDistribution);

        return ResponseEntity.ok(stats);
    }

    /**
     * Récupérer le feedback d'un utilisateur pour une recette spécifique
     * GET /api/persistance/feedbacks/utilisateur/1/recette/5
     */
    @GetMapping("/utilisateur/{userId}/recette/{recetteId}")
    public ResponseEntity<FeedbackDTO> getFeedbackByUserAndRecette(
            @PathVariable Long userId,
            @PathVariable Long recetteId) {
        return feedbackService.findByUserIdAndRecetteId(userId, recetteId)
                .map(this::enrichFeedbackDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Créer un nouveau feedback
     * POST /api/persistance/feedbacks
     */
    @PostMapping
    public ResponseEntity<FeedbackDTO> createFeedback(@RequestBody FeedbackDTO dto) {
        // Validation : vérifier que la note est entre 1 et 5
        if (dto.getNote() == null || dto.getNote() < 1 || dto.getNote() > 5) {
            return ResponseEntity.badRequest().build();
        }

        // Vérifier si un feedback existe déjà pour cet utilisateur et cette recette
        if (feedbackService.findByUserIdAndRecetteId(dto.getUserId(), dto.getRecetteId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Feedback feedback = feedbackMapper.toEntity(dto);
        Feedback saved = feedbackService.save(feedback);
        FeedbackDTO responseDto = enrichFeedbackDTO(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * Mettre à jour un feedback existant
     * PUT /api/persistance/feedbacks/1
     */
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackDTO> updateFeedback(
            @PathVariable Long id,
            @RequestBody FeedbackDTO dto) {

        // Validation : vérifier que la note est entre 1 et 5
        if (dto.getNote() == null || dto.getNote() < 1 || dto.getNote() > 5) {
            return ResponseEntity.badRequest().build();
        }

        Feedback feedback = feedbackMapper.toEntity(dto);
        Feedback updated = feedbackService.update(id, feedback);
        FeedbackDTO responseDto = enrichFeedbackDTO(updated);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * Supprimer un feedback
     * DELETE /api/persistance/feedbacks/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupérer les feedbacks récents (avec limite)
     * GET /api/persistance/feedbacks/recent?limit=10
     */
    @GetMapping("/recent")
    public ResponseEntity<List<FeedbackDTO>> getRecentFeedbacks(
            @RequestParam(defaultValue = "10") int limit) {

        List<FeedbackDTO> dtos = feedbackService.findAll().stream()
                .sorted((a, b) -> b.getDateFeedback().compareTo(a.getDateFeedback()))
                .limit(limit)
                .map(this::enrichFeedbackDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Récupérer les feedbacks par note
     * GET /api/persistance/feedbacks/by-note/5
     */
    @GetMapping("/by-note/{note}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacksByNote(@PathVariable Integer note) {
        if (note < 1 || note > 5) {
            return ResponseEntity.badRequest().build();
        }

        List<FeedbackDTO> dtos = feedbackService.findAll().stream()
                .filter(feedback -> feedback.getNote().equals(note))
                .map(this::enrichFeedbackDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Récupérer les statistiques globales des feedbacks
     * GET /api/persistance/feedbacks/stats/global
     */
    @GetMapping("/stats/global")
    public ResponseEntity<Map<String, Object>> getGlobalStats() {
        List<Feedback> allFeedbacks = feedbackService.findAll();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFeedbacks", allFeedbacks.size());

        if (!allFeedbacks.isEmpty()) {
            double averageNote = allFeedbacks.stream()
                    .mapToInt(Feedback::getNote)
                    .average()
                    .orElse(0.0);

            stats.put("globalAverageNote", averageNote);

            // Distribution des notes
            Map<Integer, Long> noteDistribution = allFeedbacks.stream()
                    .collect(Collectors.groupingBy(Feedback::getNote, Collectors.counting()));

            stats.put("noteDistribution", noteDistribution);
        } else {
            stats.put("globalAverageNote", 0.0);
            stats.put("noteDistribution", new HashMap<>());
        }

        return ResponseEntity.ok(stats);
    }

    /**
     * Méthode helper pour enrichir le FeedbackDTO avec email et titre de recette
     */
    private FeedbackDTO enrichFeedbackDTO(Feedback feedback) {
        FeedbackDTO dto = feedbackMapper.toDTO(feedback);

        // Enrichir avec l'email de l'utilisateur
        utilisateurService.findById(feedback.getUserId()).ifPresent(user ->
                dto.setUserEmail(user.getEmailAddress())
        );

        // Enrichir avec le titre de la recette
        recetteService.findById(feedback.getRecetteId()).ifPresent(recette ->
                dto.setRecetteTitre(recette.getTitre())
        );

        return dto;
    }
}