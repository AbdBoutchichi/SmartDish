package com.spingbootTemplate.univ.soa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootTemplate.univ.soa.controller.UtilisateurController;
import com.springbootTemplate.univ.soa.dto.UtilisateurDTO;
import com.springbootTemplate.univ.soa.mapper.UtilisateurMapper;
import com.springbootTemplate.univ.soa.model.Utilisateur;
import com.springbootTemplate.univ.soa.service.UtilisateurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UtilisateurControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private UtilisateurMapper utilisateurMapper;

    private UtilisateurDTO dto;
    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        UtilisateurController controller = new UtilisateurController();
        ReflectionTestUtils.setField(controller, "utilisateurService", utilisateurService);
        ReflectionTestUtils.setField(controller, "utilisateurMapper", utilisateurMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        dto = new UtilisateurDTO();
        dto.setId(1L);
        dto.setEmail("user@example.com");
        dto.setMotDePasse("password123");
        dto.setNom("Dupont");
        dto.setPrenom("Jean");
        dto.setRole(Utilisateur.Role.USER);

        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setEmail("user@example.com");
        utilisateur.setNom("Dupont");
    }

    @Test
    void getAllUtilisateurs_ReturnsList() throws Exception {
        when(utilisateurService.findAll()).thenReturn(Arrays.asList(utilisateur));
        when(utilisateurMapper.toDTO(any(Utilisateur.class))).thenReturn(dto);

        mockMvc.perform(get("/api/persistance/utilisateurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("user@example.com"));

        verify(utilisateurService).findAll();
    }

    @Test
    void getUtilisateurById_ReturnsUtilisateur_WhenFound() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurMapper.toDTO(utilisateur)).thenReturn(dto);

        mockMvc.perform(get("/api/persistance/utilisateurs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@example.com"));

        verify(utilisateurService).findById(1L);
    }

    @Test
    void getUtilisateurById_ReturnsNotFound_WhenMissing() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/persistance/utilisateurs/1"))
                .andExpect(status().isNotFound());

        verify(utilisateurService).findById(1L);
    }

    @Test
    void createUtilisateur_ReturnsBadRequest_WhenEmailMissing() throws Exception {
        dto.setEmail(null);

        mockMvc.perform(post("/api/persistance/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("L'email est obligatoire"));
    }

    @Test
    void createUtilisateur_ReturnsBadRequest_WhenEmailInvalid() throws Exception {
        dto.setEmail("invalid-email");

        mockMvc.perform(post("/api/persistance/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Format d'email invalide"));
    }

    @Test
    void createUtilisateur_ReturnsConflict_WhenEmailExists() throws Exception {
        when(utilisateurService.findByEmail("user@example.com")).thenReturn(Optional.of(utilisateur));

        mockMvc.perform(post("/api/persistance/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Un utilisateur avec cet email existe déjà"));
    }

    @Test
    void createUtilisateur_ReturnsBadRequest_WhenPasswordMissing() throws Exception {
        when(utilisateurService.findByEmail("user@example.com")).thenReturn(Optional.empty());
        dto.setMotDePasse(null);

        mockMvc.perform(post("/api/persistance/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le mot de passe est obligatoire"));
    }

    @Test
    void createUtilisateur_ReturnsBadRequest_WhenPasswordTooShort() throws Exception {
        when(utilisateurService.findByEmail("user@example.com")).thenReturn(Optional.empty());
        dto.setMotDePasse("123");

        mockMvc.perform(post("/api/persistance/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le mot de passe doit contenir au moins 6 caractères"));
    }

    @Test
    void createUtilisateur_ReturnsBadRequest_WhenNomMissing() throws Exception {
        when(utilisateurService.findByEmail("user@example.com")).thenReturn(Optional.empty());
        dto.setNom(null);

        mockMvc.perform(post("/api/persistance/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le nom est obligatoire"));
    }

    @Test
    void createUtilisateur_ReturnsBadRequest_WhenPrenomMissing() throws Exception {
        when(utilisateurService.findByEmail("user@example.com")).thenReturn(Optional.empty());
        dto.setPrenom(null);

        mockMvc.perform(post("/api/persistance/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le prénom est obligatoire"));
    }

    @Test
    void createUtilisateur_ReturnsBadRequest_WhenRoleInvalid() throws Exception {
        // set invalid role by manipulating JSON (role expects USER or ADMIN enum)
        String invalidJson = "{\"email\":\"user@example.com\",\"motDePasse\":\"password123\",\"nom\":\"Dupont\",\"prenom\":\"Jean\",\"role\":\"INVALID\"}";

        mockMvc.perform(post("/api/persistance/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUtilisateur_ReturnsCreated_WhenValid() throws Exception {
        when(utilisateurService.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(utilisateurService.saveFromDTO(any(UtilisateurDTO.class))).thenReturn(utilisateur);
        when(utilisateurMapper.toDTO(utilisateur)).thenReturn(dto);

        mockMvc.perform(post("/api/persistance/utilisateurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(utilisateurService).saveFromDTO(any(UtilisateurDTO.class));
    }

    @Test
    void updateUtilisateur_ReturnsNotFound_WhenMissing() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/persistance/utilisateurs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        verify(utilisateurService).findById(1L);
    }

    @Test
    void updateUtilisateur_ReturnsBadRequest_WhenEmailMissing() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.of(utilisateur));
        dto.setEmail(null);

        mockMvc.perform(put("/api/persistance/utilisateurs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("L'email est obligatoire"));
    }

    @Test
    void updateUtilisateur_ReturnsBadRequest_WhenEmailInvalid() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.of(utilisateur));
        dto.setEmail("bad-email");

        mockMvc.perform(put("/api/persistance/utilisateurs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Format d'email invalide"));
    }

    @Test
    void updateUtilisateur_ReturnsConflict_WhenEmailUsedByAnother() throws Exception {
        Utilisateur another = new Utilisateur();
        another.setId(2L);
        another.setEmail("other@example.com");

        when(utilisateurService.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurService.findByEmail("user@example.com")).thenReturn(Optional.of(another));

        mockMvc.perform(put("/api/persistance/utilisateurs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Un autre utilisateur utilise déjà cet email"));
    }

    @Test
    void updateUtilisateur_ReturnsBadRequest_WhenPasswordTooShort() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.of(utilisateur));
        dto.setMotDePasse("123");

        mockMvc.perform(put("/api/persistance/utilisateurs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le mot de passe doit contenir au moins 6 caractères"));
    }

    @Test
    void updateUtilisateur_ReturnsBadRequest_WhenNomOrPrenomMissing() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.of(utilisateur));
        dto.setNom(null);

        mockMvc.perform(put("/api/persistance/utilisateurs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le nom est obligatoire"));

        dto.setNom("Dupont");
        dto.setPrenom(null);

        mockMvc.perform(put("/api/persistance/utilisateurs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le prénom est obligatoire"));
    }

    @Test
    void updateUtilisateur_ReturnsOk_WhenValid() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurService.updateFromDTO(eq(1L), any(UtilisateurDTO.class))).thenReturn(utilisateur);
        when(utilisateurMapper.toDTO(utilisateur)).thenReturn(dto);

        mockMvc.perform(put("/api/persistance/utilisateurs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(utilisateurService).updateFromDTO(eq(1L), any(UtilisateurDTO.class));
    }

    @Test
    void deleteUtilisateur_ReturnsNotFound_WhenMissing() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/persistance/utilisateurs/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Utilisateur non trouvé avec l'ID: 1"));
    }

    @Test
    void deleteUtilisateur_ReturnsNoContent_WhenExists() throws Exception {
        when(utilisateurService.findById(1L)).thenReturn(Optional.of(utilisateur));

        mockMvc.perform(delete("/api/persistance/utilisateurs/1"))
                .andExpect(status().isNoContent());

        verify(utilisateurService).deleteById(1L);
    }
}

