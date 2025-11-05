package com.springbootTemplate.univ.soa.repository;

import com.springbootTemplate.univ.soa.model.Recette;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetteRepository extends JpaRepository<Recette, Long> {

    List<Recette> findByTitreContainingIgnoreCase(String titre);

    @Query("SELECT DISTINCT r FROM Recette r " +
            "JOIN r.ingredients i " +
            "WHERE i.aliment.id IN :alimentIds")
    List<Recette> findByAlimentIds(@Param("alimentIds") List<Long> alimentIds);

    @Query("SELECT r FROM Recette r " +
            "JOIN r.ingredients i " +
            "WHERE i.categorie = 'PRINCIPAL' " +
            "GROUP BY r.id " +
            "HAVING COUNT(DISTINCT i.aliment.id) = :count")
    List<Recette> findByPrincipalIngredientCount(@Param("count") Long count);
}