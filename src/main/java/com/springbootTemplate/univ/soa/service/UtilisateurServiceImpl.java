package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.dto.*;
import com.springbootTemplate.univ.soa.exception.EmailAlreadyExistsException;
import com.springbootTemplate.univ.soa.exception.UtilisateurNotFoundException;
import com.springbootTemplate.univ.soa.model.Utilisateur;
import com.springbootTemplate.univ.soa.repository.UtilisateurRepository;
import com.springbootTemplate.univ.soa.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public UtilisateurResponseDto register(UtilisateurCreateDto createDto) {
        log.info("📝 Tentative d'inscription - Email: {}", createDto.getEmail());

        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(createDto.getEmail())) {
            log.error("❌ Email déjà utilisé: {}", createDto.getEmail());
            throw new EmailAlreadyExistsException("Cet email est déjà utilisé");
        }

        // Créer l'utilisateur
        Utilisateur utilisateur = Utilisateur.builder()
                .email(createDto.getEmail())
                .motDePasse(passwordEncoder.encode(createDto.getMotDePasse()))
                .nom(createDto.getNom())
                .prenom(createDto.getPrenom())
                .telephone(createDto.getTelephone())
                .adresse(createDto.getAdresse())
                .role(createDto.getRole())
                .actif(true)
                .build();

        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
        log.info("✅ Utilisateur créé avec succès - ID: {}, Email: {}",
                savedUtilisateur.getId(), savedUtilisateur.getEmail());

        return mapToResponseDto(savedUtilisateur);
    }

    @Override
    public String login(LoginDto loginDto) {
        log.info("🔐 Tentative de connexion - Email: {}", loginDto.getEmail());

        Utilisateur utilisateur = utilisateurRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> {
                    log.error("❌ Utilisateur non trouvé: {}", loginDto.getEmail());
                    return new BadCredentialsException("Email ou mot de passe incorrect");
                });

        if (!utilisateur.getActif()) {
            log.error("❌ Compte désactivé: {}", loginDto.getEmail());
            throw new BadCredentialsException("Compte désactivé");
        }

        if (!passwordEncoder.matches(loginDto.getMotDePasse(), utilisateur.getMotDePasse())) {
            log.error("❌ Mot de passe incorrect pour: {}", loginDto.getEmail());
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }

        String token = jwtUtil.generateToken(utilisateur.getEmail(), utilisateur.getRole().name());
        log.info("✅ Connexion réussie - Email: {}", utilisateur.getEmail());

        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponseDto getUtilisateurById(Long id) {
        log.info("🔍 Recherche utilisateur par ID: {}", id);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Utilisateur non trouvé - ID: {}", id);
                    return new UtilisateurNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
                });

        return mapToResponseDto(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponseDto getUtilisateurByEmail(String email) {
        log.info("🔍 Recherche utilisateur par email: {}", email);

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("❌ Utilisateur non trouvé - Email: {}", email);
                    return new UtilisateurNotFoundException("Utilisateur non trouvé avec l'email: " + email);
                });

        return mapToResponseDto(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponseDto> getAllUtilisateurs() {
        log.info("📋 Récupération de tous les utilisateurs");

        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        log.info("✅ {} utilisateurs trouvés", utilisateurs.size());

        return utilisateurs.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UtilisateurResponseDto updateUtilisateur(Long id, UtilisateurUpdateDto updateDto) {
        log.info("📝 Mise à jour utilisateur - ID: {}", id);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Utilisateur non trouvé - ID: {}", id);
                    return new UtilisateurNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
                });

        // Mise à jour des champs
        if (updateDto.getNom() != null) {
            utilisateur.setNom(updateDto.getNom());
        }
        if (updateDto.getPrenom() != null) {
            utilisateur.setPrenom(updateDto.getPrenom());
        }
        if (updateDto.getTelephone() != null) {
            utilisateur.setTelephone(updateDto.getTelephone());
        }
        if (updateDto.getAdresse() != null) {
            utilisateur.setAdresse(updateDto.getAdresse());
        }
        if (updateDto.getNouveauMotDePasse() != null) {
            utilisateur.setMotDePasse(passwordEncoder.encode(updateDto.getNouveauMotDePasse()));
            log.info("🔐 Mot de passe mis à jour pour l'utilisateur ID: {}", id);
        }

        Utilisateur updatedUtilisateur = utilisateurRepository.save(utilisateur);
        log.info("✅ Utilisateur mis à jour avec succès - ID: {}", id);

        return mapToResponseDto(updatedUtilisateur);
    }

    @Override
    public void deleteUtilisateur(Long id) {
        log.info("🗑️ Suppression utilisateur - ID: {}", id);

        if (!utilisateurRepository.existsById(id)) {
            log.error("❌ Utilisateur non trouvé - ID: {}", id);
            throw new UtilisateurNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
        }

        utilisateurRepository.deleteById(id);
        log.info("✅ Utilisateur supprimé avec succès - ID: {}", id);
    }

    @Override
    public void activerUtilisateur(Long id) {
        log.info("✅ Activation utilisateur - ID: {}", id);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Utilisateur non trouvé - ID: {}", id);
                    return new UtilisateurNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
                });

        utilisateur.setActif(true);
        utilisateurRepository.save(utilisateur);
        log.info("✅ Utilisateur activé avec succès - ID: {}", id);
    }

    @Override
    public void desactiverUtilisateur(Long id) {
        log.info("❌ Désactivation utilisateur - ID: {}", id);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Utilisateur non trouvé - ID: {}", id);
                    return new UtilisateurNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
                });

        utilisateur.setActif(false);
        utilisateurRepository.save(utilisateur);
        log.info("✅ Utilisateur désactivé avec succès - ID: {}", id);
    }

    // Mapper
    private UtilisateurResponseDto mapToResponseDto(Utilisateur utilisateur) {
        return UtilisateurResponseDto.builder()
                .id(utilisateur.getId())
                .email(utilisateur.getEmail())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .telephone(utilisateur.getTelephone())
                .adresse(utilisateur.getAdresse())
                .role(utilisateur.getRole())
                .actif(utilisateur.getActif())
                .dateCreation(utilisateur.getDateCreation())
                .dateModification(utilisateur.getDateModification())
                .build();
    }
}