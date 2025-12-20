package com.springbootTemplate.univ.soa.client;

import com.springbootTemplate.univ.soa.dto.MsPersistanceUtilisateurDto;
import com.springbootTemplate.univ.soa.dto.UtilisateurCreateDto;
import com.springbootTemplate.univ.soa.dto.UtilisateurResponseDto;
import com.springbootTemplate.univ.soa.dto.UtilisateurUpdateDto;
import com.springbootTemplate.univ.soa.exception.UtilisateurNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersistanceClient {

    private final RestTemplate restTemplate;

    @Value("${persistance.service.url}")
    private String persistanceServiceUrl;

    private static final String UTILISATEURS_PATH = "/api/persistance/utilisateurs";

    /**
     * Créer un utilisateur via ms-persistance
     */
    public UtilisateurResponseDto createUtilisateur(UtilisateurCreateDto createDto) {
        String url = persistanceServiceUrl + UTILISATEURS_PATH;

        try {
            log.info("📤 Appel POST vers ms-persistance: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<UtilisateurCreateDto> request = new HttpEntity<>(createDto, headers);

            ResponseEntity<UtilisateurResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    UtilisateurResponseDto.class
            );

            log.info("✅ Utilisateur créé avec succès via ms-persistance");
            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("❌ Erreur lors de la création de l'utilisateur: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Récupérer un utilisateur par ID
     */
    public UtilisateurResponseDto getUtilisateurById(Long id) {
        String url = persistanceServiceUrl + UTILISATEURS_PATH + "/" + id;

        try {
            log.info("📤 Appel GET vers ms-persistance: {}", url);

            ResponseEntity<UtilisateurResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    UtilisateurResponseDto.class
            );

            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            log.error("❌ Utilisateur non trouvé avec l'ID: {}", id);
            throw new UtilisateurNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
        }
    }

    /**
     * Récupérer un utilisateur par email
     */
    public UtilisateurResponseDto getUtilisateurByEmail(String email) {
        String url = persistanceServiceUrl + UTILISATEURS_PATH + "/email/" + email;

        try {
            log.info("📤 Appel GET vers ms-persistance pour l'email: {}", email);

            ResponseEntity<UtilisateurResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    UtilisateurResponseDto.class
            );

            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            log.error("❌ Utilisateur non trouvé avec l'email: {}", email);
            throw new UtilisateurNotFoundException("Utilisateur non trouvé avec l'email: " + email);
        }
    }

    /**
     * Récupérer tous les utilisateurs
     */
    public List<UtilisateurResponseDto> getAllUtilisateurs() {
        String url = persistanceServiceUrl + UTILISATEURS_PATH;

        try {
            log.info("📤 Appel GET vers ms-persistance pour récupérer tous les utilisateurs");

            ResponseEntity<List<UtilisateurResponseDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<UtilisateurResponseDto>>() {}
            );

            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("❌ Erreur lors de la récupération des utilisateurs: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Mettre à jour un utilisateur
     */
    public UtilisateurResponseDto updateUtilisateur(Long id, UtilisateurUpdateDto updateDto) {
        String url = persistanceServiceUrl + UTILISATEURS_PATH + "/" + id;

        try {
            log.info("📤 Appel PUT vers ms-persistance: {}", url);

            // 1. Récupérer l'utilisateur existant pour avoir tous les champs obligatoires
            UtilisateurResponseDto existingUser = getUtilisateurById(id);
            log.debug("Utilisateur existant récupéré: email={}, nom={}, prenom={}",
                    existingUser.getEmail(), existingUser.getNom(), existingUser.getPrenom());

            // 2. Construire un DTO complet pour MS-Persistance en fusionnant les données
            MsPersistanceUtilisateurDto fullDto = MsPersistanceUtilisateurDto.builder()
                    .id(existingUser.getId())
                    .email(existingUser.getEmail()) // Obligatoire - on garde l'existant
                    .nom(updateDto.getNom() != null ? updateDto.getNom() : existingUser.getNom())
                    .prenom(updateDto.getPrenom() != null ? updateDto.getPrenom() : existingUser.getPrenom())
                    .motDePasse(updateDto.getNouveauMotDePasse()) // null = pas de changement
                    .role(existingUser.getRole())
                    .actif(existingUser.getActif())
                    .alimentsExclusIds(updateDto.getAlimentsExclusIds() != null ?
                            updateDto.getAlimentsExclusIds() : existingUser.getAlimentsExclusIds())
                    .dateCreation(existingUser.getDateCreation())
                    .dateModification(existingUser.getDateModification())
                    .build();

            log.debug("DTO complet construit: email={}, nom={}, prenom={}, motDePasse={}",
                    fullDto.getEmail(), fullDto.getNom(), fullDto.getPrenom(),
                    fullDto.getMotDePasse() != null ? "[ENCODÉ]" : "[NON MODIFIÉ]");

            // 3. Envoyer le DTO complet à MS-Persistance
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<MsPersistanceUtilisateurDto> request = new HttpEntity<>(fullDto, headers);

            ResponseEntity<UtilisateurResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    request,
                    UtilisateurResponseDto.class
            );

            log.info("✅ Utilisateur mis à jour avec succès via ms-persistance");
            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            log.error("❌ Utilisateur non trouvé avec l'ID: {}", id);
            throw new UtilisateurNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
        } catch (HttpClientErrorException e) {
            log.error("❌ Erreur HTTP {} lors de la mise à jour: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("❌ Erreur inattendue lors de la mise à jour: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour de l'utilisateur", e);
        }
    }

    /**
     * Supprimer un utilisateur
     */
    public void deleteUtilisateur(Long id) {
        String url = persistanceServiceUrl + UTILISATEURS_PATH + "/" + id;

        try {
            log.info("📤 Appel DELETE vers ms-persistance: {}", url);

            restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            log.info("✅ Utilisateur supprimé avec succès via ms-persistance");

        } catch (HttpClientErrorException.NotFound e) {
            log.error("❌ Utilisateur non trouvé avec l'ID: {}", id);
            throw new UtilisateurNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
        }
    }

    /**
     * Vérifier si un email existe déjà
     */
    public boolean existsByEmail(String email) {
        try {
            getUtilisateurByEmail(email);
            return true;
        } catch (UtilisateurNotFoundException e) {
            return false;
        }
    }
}