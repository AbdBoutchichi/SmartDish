package com.springbootTemplate.univ.soa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "ingredient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recette_id", nullable = false)
    @JsonBackReference
    private Recette recette;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aliment_id", nullable = false)
    private Aliment aliment;

    @Column
    private Integer quantite;

    @Column(length = 20)
    private String unite; // g, ml, cuillère, pièce

    @Column(length = 20)
    private String categorie; // PRINCIPAL, SECONDAIRE
}