package com.springbootTemplate.univ.soa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "aliment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aliment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(length = 50)
    private String categorie; // LEGUME, VIANDE, POISSON, CEREALE, FRUIT, EPICE
}