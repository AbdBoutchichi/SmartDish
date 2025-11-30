package com.spingbootTemplate.univ.soa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootTemplate.univ.soa.controller.AlimentController;
import com.springbootTemplate.univ.soa.dto.AlimentDTO;
import com.springbootTemplate.univ.soa.mapper.AlimentMapper;
import com.springbootTemplate.univ.soa.model.Aliment;
import com.springbootTemplate.univ.soa.service.AlimentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
public class AlimentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AlimentService alimentService;

    @Mock
    private AlimentMapper alimentMapper;


    private final ObjectMapper objectMapper = new ObjectMapper();

    private AlimentDTO alimentDTO;
    private Aliment aliment;

    @BeforeEach
    void setUp() {

        AlimentController controller = new AlimentController();
        ReflectionTestUtils.setField(controller, "alimentService", alimentService);
        ReflectionTestUtils.setField(controller, "alimentMapper", alimentMapper);


        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        alimentDTO = new AlimentDTO();
        alimentDTO.setId(1L);
        alimentDTO.setNom("Tomate");
        alimentDTO.setCategorie(Aliment.CategorieAliment.LEGUME);

        aliment = new Aliment();
        aliment.setId(1L);
        aliment.setNom("Tomate");
        aliment.setCategorie(Aliment.CategorieAliment.LEGUME);
    }

    @Test
    void getAllAliments_ShouldReturnList() throws Exception {
        List<Aliment> aliments = Collections.singletonList(aliment);
        when(alimentService.findAll()).thenReturn(aliments);
        when(alimentMapper.toDTO(any(Aliment.class))).thenReturn(alimentDTO);

        mockMvc.perform(get("/api/persistance/aliments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nom").value("Tomate"));

        verify(alimentService).findAll();
        verify(alimentMapper).toDTO(aliment);
    }

    @Test
    void getAlimentById_ShouldReturnAliment_WhenFound() throws Exception {
        when(alimentService.findById(1L)).thenReturn(Optional.of(aliment));
        when(alimentMapper.toDTO(aliment)).thenReturn(alimentDTO);

        mockMvc.perform(get("/api/persistance/aliments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nom").value("Tomate"));

        verify(alimentService).findById(1L);
        verify(alimentMapper).toDTO(aliment);
    }

    @Test
    void getAlimentById_ShouldReturnNotFound_WhenNotFound() throws Exception {
        when(alimentService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/persistance/aliments/1"))
                .andExpect(status().isNotFound());

        verify(alimentService).findById(1L);
    }

    @Test
    void createAliment_ShouldReturnCreated_WhenValid() throws Exception {
        when(alimentService.findByNom("Tomate")).thenReturn(Optional.empty());
        when(alimentMapper.toEntity(alimentDTO)).thenReturn(aliment);
        when(alimentService.save(aliment)).thenReturn(aliment);
        when(alimentMapper.toDTO(aliment)).thenReturn(alimentDTO);

        mockMvc.perform(post("/api/persistance/aliments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alimentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nom").value("Tomate"));

        verify(alimentService).findByNom("Tomate");
        verify(alimentService).save(aliment);
    }

    @Test
    void createAliment_ShouldReturnBadRequest_WhenNameNull() throws Exception {
        alimentDTO.setNom(null);

        mockMvc.perform(post("/api/persistance/aliments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alimentDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le nom de l'aliment est obligatoire"));
    }

    @Test
    void createAliment_ShouldReturnBadRequest_WhenNameEmpty() throws Exception {
        alimentDTO.setNom("");

        mockMvc.perform(post("/api/persistance/aliments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alimentDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le nom de l'aliment est obligatoire"));
    }

    @Test
    void createAliment_ShouldReturnBadRequest_WhenNameTooShort() throws Exception {
        alimentDTO.setNom("A");

        mockMvc.perform(post("/api/persistance/aliments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alimentDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le nom doit contenir au moins 2 caractères"));
    }

    @Test
    void createAliment_ShouldReturnBadRequest_WhenNameTooLong() throws Exception {
        alimentDTO.setNom("A".repeat(101));

        mockMvc.perform(post("/api/persistance/aliments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alimentDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Le nom ne peut pas dépasser 100 caractères"));
    }

    @Test
    void createAliment_ShouldReturnConflict_WhenNameExists() throws Exception {
        when(alimentService.findByNom("Tomate")).thenReturn(Optional.of(aliment));

        mockMvc.perform(post("/api/persistance/aliments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alimentDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Un aliment avec ce nom existe déjà"));
    }

    @Test
    void createAliment_ShouldReturnBadRequest_WhenCategoryNull() throws Exception {
        alimentDTO.setCategorie(null);

        mockMvc.perform(post("/api/persistance/aliments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alimentDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La catégorie est obligatoire"));
    }

    @Test
    void createAliment_ShouldReturnBadRequest_WhenCategoryInvalid() throws Exception {
        String invalidJson = "{\"nom\":\"Tomate\",\"categorie\":\"INVALID\"}";

        mockMvc.perform(post("/api/persistance/aliments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAliment_ShouldReturnOk_WhenValid() throws Exception {
        when(alimentService.findById(1L)).thenReturn(Optional.of(aliment));
        when(alimentService.findByNom("Tomate")).thenReturn(Optional.of(aliment));
        when(alimentMapper.toEntity(alimentDTO)).thenReturn(aliment);
        when(alimentService.update(1L, aliment)).thenReturn(aliment);
        when(alimentMapper.toDTO(aliment)).thenReturn(alimentDTO);

        mockMvc.perform(put("/api/persistance/aliments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alimentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(alimentService).update(1L, aliment);
    }

    @Test
    void updateAliment_ShouldReturnNotFound_WhenNotExists() throws Exception {
        when(alimentService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/persistance/aliments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alimentDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Aliment non trouvé avec l'ID: 1"));
    }

    @Test
    void updateAliment_ReturnsConflict_WhenServiceThrowsIllegalArgumentException() throws Exception {
        when(alimentService.findById(1L)).thenReturn(Optional.of(aliment));
        when(alimentMapper.toEntity(alimentDTO)).thenReturn(aliment);
        when(alimentService.update(1L, aliment))
                .thenThrow(new IllegalArgumentException("Un autre aliment utilise déjà ce nom"));

        mockMvc.perform(put("/api/persistance/aliments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alimentDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Un autre aliment utilise déjà ce nom"));
    }

    @Test
    void deleteAliment_ShouldReturnNoContent_WhenExists() throws Exception {
        when(alimentService.findById(1L)).thenReturn(Optional.of(aliment));

        mockMvc.perform(delete("/api/persistance/aliments/1"))
                .andExpect(status().isNoContent());

        verify(alimentService).deleteById(1L);
    }

    @Test
    void deleteAliment_ShouldReturnNotFound_WhenNotExists() throws Exception {
        when(alimentService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/persistance/aliments/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Aliment non trouvé avec l'ID: 1"));
    }
}
