package com.springbootTemplate.univ.soa.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration web de l'application
 * Note: Le bean RestTemplate est défini dans RestTemplateConfig
 */
@Configuration
public class WebConfig {
    // Configuration web générale
    // Le RestTemplate est fourni par RestTemplateConfig avec timeouts configurés
}
