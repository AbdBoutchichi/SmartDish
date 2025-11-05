package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.exception.ResourceNotFoundException;
import com.springbootTemplate.univ.soa.model.Aliment;
import com.springbootTemplate.univ.soa.repository.AlimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AlimentService {

    @Autowired
    private AlimentRepository alimentRepository;

    public List<Aliment> findAll() {
        return alimentRepository.findAll();
    }

    public Optional<Aliment> findById(Long id) {
        return alimentRepository.findById(id);
    }

    public Optional<Aliment> findByNom(String nom) {
        return alimentRepository.findByNomIgnoreCase(nom);
    }

    public List<Aliment> findByCategorie(String categorie) {
        return alimentRepository.findByCategorie(categorie);
    }

    public List<Aliment> searchByNom(String nom) {
        return alimentRepository.findByNomContainingIgnoreCase(nom);
    }

    @Transactional
    public Aliment save(Aliment aliment) {
        // IMPORTANT : Forcer l'ID à null pour la création
        aliment.setId(null);
        return alimentRepository.save(aliment);
    }

    @Transactional
    public Aliment update(Long id, Aliment aliment) {
        Aliment existing = alimentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aliment non trouvé avec l'ID: " + id));

        existing.setNom(aliment.getNom());
        existing.setCategorie(aliment.getCategorie());

        return alimentRepository.save(existing);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!alimentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aliment non trouvé avec l'ID: " + id);
        }
        alimentRepository.deleteById(id);
    }
}