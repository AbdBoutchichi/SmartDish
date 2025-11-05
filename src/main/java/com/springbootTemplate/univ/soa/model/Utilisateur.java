package com.springbootTemplate.univ.soa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "utilisateur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email_address", unique = true, nullable = false, length = 100)
    private String emailAddress;

    @Column(nullable = false)
    private String password;

    @Column(name = "regime_alimentaire", length = 50)
    private String regimeAlimentaire; // VEGETARIEN, VEGETALIEN, GLUTEN_FREE

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "allergene",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "aliment_id")
    )
    private Set<Aliment> allergenes = new HashSet<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}