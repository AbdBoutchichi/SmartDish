package com.springbootTemplate.univ.soa.controller;

import com.springbootTemplate.univ.soa.dto.LoginRequestDTO;
import com.springbootTemplate.univ.soa.dto.UtilisateurCreateDTO;
import com.springbootTemplate.univ.soa.dto.UtilisateurDTO;
import com.springbootTemplate.univ.soa.mapper.UtilisateurMapper;
import com.springbootTemplate.univ.soa.model.Utilisateur;
import com.springbootTemplate.univ.soa.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/persistance/utilisateurs")
@CrossOrigin(origins = "*")
@Tag(name = "Utilisateurs", description = "API de gestion des utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private UtilisateurMapper utilisateurMapper;

    @Operation(summary = "Récupérer tous les utilisateurs")
    @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée")
    @GetMapping
    public ResponseEntity<List<UtilisateurDTO>> getAllUtilisateurs() {
        List<UtilisateurDTO> dtos = utilisateurService.findAll().stream()
                .map(utilisateurMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Récupérer un utilisateur par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> getUtilisateurById(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id) {
        return utilisateurService.findById(id)
                .map(utilisateurMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Récupérer un utilisateur par email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UtilisateurDTO> getUtilisateurByEmail(
            @Parameter(description = "Email de l'utilisateur") @PathVariable String email) {
        return utilisateurService.findByEmail(email)
                .map(utilisateurMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un nouvel utilisateur (inscription)",
            description = "Le mot de passe sera automatiquement hashé avec BCrypt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès",
                    content = @Content(schema = @Schema(implementation = UtilisateurDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    @PostMapping
    public ResponseEntity<?> createUtilisateur(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Données du nouvel utilisateur (mot de passe sera hashé)",
                    required = true
            )
            @RequestBody UtilisateurCreateDTO createDTO) {

        // Vérifier si l'email existe déjà
        if (utilisateurService.findByEmail(createDTO.getEmailAddress()).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Un utilisateur avec cet email existe déjà");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        // Convertir CreateDTO vers Entity
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmailAddress(createDTO.getEmailAddress());
        utilisateur.setPassword(createDTO.getPassword()); // Sera hashé dans le service
        utilisateur.setRegimeAlimentaire(createDTO.getRegimeAlimentaire());

        // Sauvegarder avec les allergènes
        Utilisateur saved = utilisateurService.createWithAllergenes(
                utilisateur,
                createDTO.getAllergeneIds()
        );

        UtilisateurDTO responseDTO = utilisateurMapper.toDTO(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Connexion d'un utilisateur",
            description = "Vérifie l'email et le mot de passe hashé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie",
                    content = @Content(schema = @Schema(implementation = UtilisateurDTO.class))),
            @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Identifiants de connexion",
                    required = true
            )
            @RequestBody LoginRequestDTO loginRequest) {

        return utilisateurService.authenticate(
                        loginRequest.getEmailAddress(),
                        loginRequest.getPassword()
                )
                .map(utilisateur -> {
                    UtilisateurDTO dto = utilisateurMapper.toDTO(utilisateur);
                    return ResponseEntity.ok((Object) dto);
                })
                .orElseGet(() -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Email ou mot de passe incorrect");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
                });
    }

    @Operation(summary = "Mettre à jour un utilisateur",
            description = "Si un nouveau mot de passe est fourni, il sera hashé automatiquement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> updateUtilisateur(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles données de l'utilisateur",
                    required = true
            )
            @RequestBody UtilisateurCreateDTO updateDTO) {

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmailAddress(updateDTO.getEmailAddress());
        utilisateur.setPassword(updateDTO.getPassword()); // Sera hashé dans le service si fourni
        utilisateur.setRegimeAlimentaire(updateDTO.getRegimeAlimentaire());

        Utilisateur updated = utilisateurService.update(id, utilisateur);
        return ResponseEntity.ok(utilisateurMapper.toDTO(updated));
    }

    @Operation(summary = "Supprimer un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utilisateur supprimé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilisateur(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id) {
        utilisateurService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ajouter un allergène à un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Allergène ajouté"),
            @ApiResponse(responseCode = "404", description = "Utilisateur ou aliment non trouvé")
    })
    @PostMapping("/{userId}/allergenes/{alimentId}")
    public ResponseEntity<Void> addAllergene(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long userId,
            @Parameter(description = "ID de l'aliment allergène") @PathVariable Long alimentId) {
        utilisateurService.addAllergene(userId, alimentId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Retirer un allergène d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Allergène retiré"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @DeleteMapping("/{userId}/allergenes/{alimentId}")
    public ResponseEntity<Void> removeAllergene(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long userId,
            @Parameter(description = "ID de l'aliment allergène") @PathVariable Long alimentId) {
        utilisateurService.removeAllergene(userId, alimentId);
        return ResponseEntity.noContent().build();
    }
}