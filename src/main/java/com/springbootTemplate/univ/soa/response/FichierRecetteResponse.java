package com.springbootTemplate.univ.soa.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FichierRecetteResponse {
    private Long id;
    private Long recetteId;
    private String nomOriginal;
    private String nomStockage;
    private String contentType;
    private Long taille;
    private String type;
    private String url;
    private LocalDateTime dateCreation;
}

