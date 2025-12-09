package com.springbootTemplate.univ.soa.service;

import com.springbootTemplate.univ.soa.request.RecetteCreateRequest;
import com.springbootTemplate.univ.soa.request.RecetteSearchRequest;
import com.springbootTemplate.univ.soa.request.RecetteUpdateRequest;
import com.springbootTemplate.univ.soa.response.RecetteResponse;
import com.springbootTemplate.univ.soa.response.RecetteStatsResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RecetteService {
    RecetteResponse createRecette(RecetteCreateRequest request);
    List<RecetteResponse> getAllRecettes();
    RecetteResponse getRecetteById(Long id);
    CompletableFuture<RecetteResponse> getRecetteByIdAsync(Long id);
    List<RecetteResponse> searchRecettes(RecetteSearchRequest searchRequest);
    List<RecetteResponse> getRecettesByCategorie(String categorie);
    RecetteStatsResponse getRecetteStats(Long id);
    RecetteResponse updateRecette(Long id, RecetteUpdateRequest request);
    void deleteRecette(Long id);
    boolean recetteExists(Long id);
    List<RecetteResponse> getPopularRecettes(int limit);
    List<RecetteResponse> getRecentRecettes(int limit);
}
