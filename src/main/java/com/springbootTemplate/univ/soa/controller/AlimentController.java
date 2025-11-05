package com.springbootTemplate.univ.soa.controller;

import com.springbootTemplate.univ.soa.model.Aliment;
import com.springbootTemplate.univ.soa.service.AlimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persistance/aliments")
public class AlimentController {

    @Autowired
    private AlimentService alimentService;

    @GetMapping
    public ResponseEntity<List<Aliment>> getAllAliments(
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) String search) {

        if (categorie != null && !categorie.isEmpty()) {
            return ResponseEntity.ok(alimentService.findByCategorie(categorie));
        }

        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(alimentService.searchByNom(search));
        }

        return ResponseEntity.ok(alimentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aliment> getAlimentById(@PathVariable Long id) {
        return alimentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nom/{nom}")
    public ResponseEntity<Aliment> getAlimentByNom(@PathVariable String nom) {
        return alimentService.findByNom(nom)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Aliment> createAliment(@RequestBody Aliment aliment) {
        Aliment saved = alimentService.save(aliment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aliment> updateAliment(
            @PathVariable Long id,
            @RequestBody Aliment aliment) {
        return ResponseEntity.ok(alimentService.update(id, aliment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAliment(@PathVariable Long id) {
        alimentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}