package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.exception.ResourceNotFoundException;
import com.springbootTemplate.univ.soa.model.Aliment;
import com.springbootTemplate.univ.soa.model.Utilisateur;
import com.springbootTemplate.univ.soa.repository.AlimentRepository;
import com.springbootTemplate.univ.soa.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private AlimentRepository alimentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    public Optional<Utilisateur> findById(Long id) {
        return utilisateurRepository.findById(id);
    }

    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmailAddress(email);
    }

    @Transactional
    public Utilisateur save(Utilisateur utilisateur) {
        // IMPORTANT : Forcer l'ID à null pour la création
        utilisateur.setId(null);

        // IMPORTANT : Hasher le mot de passe avant de le sauvegarder
        if (utilisateur.getPassword() != null && !utilisateur.getPassword().isEmpty()) {
            utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        }

        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public Utilisateur createWithAllergenes(Utilisateur utilisateur, Set<Long> allergeneIds) {
        // IMPORTANT : Forcer l'ID à null
        utilisateur.setId(null);

        // IMPORTANT : Hasher le mot de passe avant de le sauvegarder
        if (utilisateur.getPassword() != null && !utilisateur.getPassword().isEmpty()) {
            utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        }

        // Ajouter les allergènes si fournis
        if (allergeneIds != null && !allergeneIds.isEmpty()) {
            Set<Aliment> allergenes = new HashSet<>();
            for (Long alimentId : allergeneIds) {
                Aliment aliment = alimentRepository.findById(alimentId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Aliment non trouvé avec l'ID: " + alimentId
                        ));
                allergenes.add(aliment);
            }
            utilisateur.setAllergenes(allergenes);
        }

        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public Utilisateur update(Long id, Utilisateur utilisateur) {
        Utilisateur existing = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        existing.setEmailAddress(utilisateur.getEmailAddress());

        // Ne mettre à jour le mot de passe que s'il est fourni et le hasher
        if (utilisateur.getPassword() != null && !utilisateur.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        }

        existing.setRegimeAlimentaire(utilisateur.getRegimeAlimentaire());

        return utilisateurRepository.save(existing);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
        }
        utilisateurRepository.deleteById(id);
    }

    @Transactional
    public void addAllergene(Long userId, Long alimentId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Aliment aliment = alimentRepository.findById(alimentId)
                .orElseThrow(() -> new ResourceNotFoundException("Aliment non trouvé"));

        utilisateur.getAllergenes().add(aliment);
        utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void removeAllergene(Long userId, Long alimentId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        utilisateur.getAllergenes().removeIf(a -> a.getId().equals(alimentId));
        utilisateurRepository.save(utilisateur);
    }

    /**
     * Méthode pour vérifier un mot de passe lors de la connexion
     */
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Méthode pour authentifier un utilisateur
     */
    public Optional<Utilisateur> authenticate(String email, String rawPassword) {
        Optional<Utilisateur> utilisateur = findByEmail(email);

        if (utilisateur.isPresent() && checkPassword(rawPassword, utilisateur.get().getPassword())) {
            return utilisateur;
        }

        return Optional.empty();
    }
}