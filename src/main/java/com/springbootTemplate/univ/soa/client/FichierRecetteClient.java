package com.springbootTemplate.univ.soa.client;


import com.springbootTemplate.univ.soa.response.FichierRecetteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FichierRecetteClient {

    private final RestTemplate restTemplate;

    @Value("${ms.persistance.url:http://localhost:8090}")
    private String persistanceServiceUrl;

    public FichierRecetteResponse uploadImage(Long recetteId, MultipartFile file) {
        String url = persistanceServiceUrl + "/api/persistance/recettes/" + recetteId + "/fichiers/images";
        log.info("POST {} - Upload image", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<FichierRecetteResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    FichierRecetteResponse.class
            );

            return response.getBody();

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier", e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'upload de l'image", e);
        }
    }

    public FichierRecetteResponse uploadDocument(Long recetteId, MultipartFile file) {
        String url = persistanceServiceUrl + "/api/persistance/recettes/" + recetteId + "/fichiers/documents";
        log.info("POST {} - Upload document", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<FichierRecetteResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    FichierRecetteResponse.class
            );

            return response.getBody();

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier", e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'upload du document", e);
        }
    }

    public List<FichierRecetteResponse> getFichiersByRecette(Long recetteId) {
        String url = persistanceServiceUrl + "/api/persistance/recettes/" + recetteId + "/fichiers";
        log.info("GET {}", url);

        ResponseEntity<List<FichierRecetteResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FichierRecetteResponse>>() {}
        );

        return response.getBody();
    }

    public List<FichierRecetteResponse> getImagesByRecette(Long recetteId) {
        String url = persistanceServiceUrl + "/api/persistance/recettes/" + recetteId + "/fichiers/images";
        log.info("GET {}", url);

        ResponseEntity<List<FichierRecetteResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FichierRecetteResponse>>() {}
        );

        return response.getBody();
    }

    public List<FichierRecetteResponse> getDocumentsByRecette(Long recetteId) {
        String url = persistanceServiceUrl + "/api/persistance/recettes/" + recetteId + "/fichiers/documents";
        log.info("GET {}", url);

        ResponseEntity<List<FichierRecetteResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FichierRecetteResponse>>() {}
        );

        return response.getBody();
    }

    public ResponseEntity<Resource> downloadFichier(Long recetteId, Long fichierId) {
        String url = persistanceServiceUrl + "/api/persistance/recettes/" + recetteId + "/fichiers/" + fichierId + "/download";
        log.info("GET {}", url);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Resource.class
        );
    }

    public FichierRecetteResponse getFichierMetadata(Long recetteId, Long fichierId) {
        String url = persistanceServiceUrl + "/api/persistance/recettes/" + recetteId + "/fichiers/" + fichierId;
        log.info("GET {}", url);

        ResponseEntity<FichierRecetteResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                FichierRecetteResponse.class
        );

        return response.getBody();
    }

    public void deleteFichier(Long recetteId, Long fichierId) {
        String url = persistanceServiceUrl + "/api/persistance/recettes/" + recetteId + "/fichiers/" + fichierId;
        log.info("DELETE {}", url);
        restTemplate.delete(url);
    }

    public void deleteAllFichiersByRecette(Long recetteId) {
        String url = persistanceServiceUrl + "/api/persistance/recettes/" + recetteId + "/fichiers";
        log.info("DELETE {}", url);
        restTemplate.delete(url);
    }

    private static class MultipartInputStreamFileResource extends InputStreamResource {
        private final String filename;

        public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() throws IOException {
            return -1;
        }
    }
}

