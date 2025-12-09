package com.springbootTemplate.univ.soa.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration du cache pour optimiser les performances
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "recettes",           // Cache pour toutes les recettes
                "recette",            // Cache pour une recette spécifique
                "recettesByCategorie",// Cache par catégorie
                "recetteStats"        // Cache des statistiques
        );

        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterWrite(10, TimeUnit.MINUTES)  // Expiration après 10 minutes
                .expireAfterAccess(5, TimeUnit.MINUTES)  // Expiration si pas accédé pendant 5 min
                .recordStats();                          // Enregistrer les statistiques du cache
    }
}

