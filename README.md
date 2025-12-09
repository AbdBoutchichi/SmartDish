# 🍽️ MS-Recette - Microservice de Gestion de Recettes

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com)
[![Coverage](https://img.shields.io/badge/coverage-60%25-yellow)](https://github.com)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-green)](https://spring.io/projects/spring-boot)

Microservice orchestrateur pour la gestion de recettes de cuisine. Il communique avec MS-Persistance via HTTP/REST pour toutes les opérations de persistance.

## 📋 Table des Matières

- [Architecture](#architecture)
- [Technologies](#technologies)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Utilisation](#utilisation)
- [API Documentation](#api-documentation)
- [Tests](#tests)
- [CI/CD](#cicd)
- [Déploiement](#déploiement)

---

## 🏗️ Architecture

```
Client (Postman/Frontend)
         ↓
MS-Recette (Port 8081)
  - Orchestration
  - Cache (Caffeine)
  - Validation
  - API REST publique
         ↓ HTTP
MS-Persistance (Port 8090)
  - Gestion CRUD
  - Accès base de données
  - Persistance MySQL/MongoDB/MinIO
         ↓ JDBC
Bases de Données (MySQL 3307, MongoDB 27017, MinIO 9000)
```

### Séparation des Responsabilités

| Fonctionnalité | MS-Recette | MS-Persistance |
|----------------|------------|----------------|
| Endpoints publics | ✅ | ❌ |
| Cache | ✅ | ❌ |
| Validation | ✅ | ❌ |
| Communication HTTP | ✅ | ❌ |
| JDBC/JPA | ❌ | ✅ |
| Accès base de données | ❌ | ✅ |

---

## 🛠️ Technologies

- **Java 21** - Langage de programmation
- **Spring Boot 3.5.6** - Framework principal
- **Spring Web** - API REST
- **RestTemplate** - Client HTTP
- **Caffeine Cache** - Cache en mémoire
- **Lombok** - Réduction de code boilerplate
- **SpringDoc OpenAPI** - Documentation Swagger
- **JaCoCo** - Couverture de code
- **JUnit 5 & Mockito** - Tests unitaires
- **Docker** - Containerisation
- **GitHub Actions** - CI/CD

---

## 📦 Prérequis

- **Java 21** ou supérieur
- **Maven 3.9+**
- **Docker** & **Docker Compose**
- **Git**

---

## 🚀 Installation

### 1. Cloner le Repository

```bash
git clone https://github.com/votre-repo/ms-recette.git
cd ms-recette
```

### 2. Configuration

Le fichier `.env` contient les variables d'environnement :

```env
SERVER_PORT=8081
MS_PERSISTANCE_URL=http://ms-persistance:8090
LOG_LEVEL_ROOT=INFO
```

### 3. Démarrer les Services

```powershell
# Démarrer tous les services (MS-Recette + MS-Persistance + Bases de données)
docker-compose up -d

# Vérifier le statut
docker-compose ps

# Voir les logs
docker logs ms-recette-local --tail=50
```

### 4. Compilation Locale

```bash
# Compiler le projet
mvn clean package -DskipTests

# Exécuter les tests
mvn test

# Générer le rapport de couverture
mvn test jacoco:report
```

---

## 💻 Utilisation

### Health Checks

```bash
# MS-Recette
curl http://localhost:8081/actuator/health

# MS-Persistance
curl http://localhost:8090/actuator/health
```

### Accès Swagger UI

- **MS-Recette** : http://localhost:8081/swagger-ui.html
- **MS-Persistance** : http://localhost:8090/swagger-ui.html

### Interfaces d'Administration

- **PhpMyAdmin (MySQL)** : http://localhost:8085
- **Mongo Express (MongoDB)** : http://localhost:8082
- **MinIO Console** : http://localhost:9001

---

## 📚 API Documentation

### Endpoints Principaux

#### Recettes

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/recettes` | Créer une recette |
| `GET` | `/api/recettes` | Liste toutes les recettes |
| `GET` | `/api/recettes/{id}` | Récupérer une recette |
| `POST` | `/api/recettes/search` | Rechercher des recettes |
| `GET` | `/api/recettes/categorie/{categorie}` | Filtrer par catégorie |
| `GET` | `/api/recettes/populaires` | Recettes populaires |
| `GET` | `/api/recettes/recentes` | Recettes récentes |
| `PUT` | `/api/recettes/{id}` | Mettre à jour une recette |
| `DELETE` | `/api/recettes/{id}` | Supprimer une recette |
| `GET` | `/api/recettes/{id}/exists` | Vérifier l'existence |
| `GET` | `/api/recettes/{id}/stats` | Statistiques d'une recette |

### Exemple - Créer une Recette

```bash
POST http://localhost:8081/api/recettes
Content-Type: application/json

{
  "titre": "Spaghetti Carbonara",
  "description": "Recette italienne traditionnelle",
  "tempsPreparation": 15,
  "tempsCuisson": 20,
  "nombrePersonnes": 4,
  "difficulte": "FACILE",
  "categorie": "PLAT_PRINCIPAL",
  "ingredients": [
    {
      "alimentId": 1,
      "quantite": 400.0,
      "unite": "GRAMME"
    }
  ]
}
```

### Catégories Valides

- `ENTREE`
- `PLAT_PRINCIPAL`
- `DESSERT`
- `BOISSON`
- `APERITIF`
- `ACCOMPAGNEMENT`

### Difficultés Valides

- `FACILE`
- `MOYEN`
- `DIFFICILE`

---

## 🧪 Tests

### Exécuter les Tests

```bash
# Tous les tests
mvn clean test

# Tests unitaires seulement
mvn test -Dtest=RecetteServiceImplTest

# Tests d'intégration seulement
mvn test -Dtest=RecetteControllerTest

# Génération du rapport de couverture
mvn test jacoco:report
```

### Résultats des Tests

- **Tests Unitaires** : 14 tests (RecetteServiceImplTest)
- **Tests d'Intégration** : 8 tests (RecetteControllerTest)
- **Tests HomeController** : 10 tests
- **Total** : 32 tests

### Couverture de Code

- **Objectif** : ≥ 60%
- **Rapport** : `target/site/jacoco/index.html`

```bash
# Vérifier la couverture
mvn jacoco:check

# Ouvrir le rapport
start target/site/jacoco/index.html
```

---

## 🔄 CI/CD

Le projet utilise GitHub Actions pour l'intégration et le déploiement continus.

### Workflows

1. **build-maven.yml** - Compilation Maven
2. **check-coverage.yml** - Vérification couverture ≥ 60%
3. **build-docker-image.yml** - Construction image Docker
4. **integration-tests.yml** - Tests d'intégration
5. **sonar-analysis.yml** - Analyse SonarQube
6. **deploy-kubernetes.yml** - Déploiement K8s

### Pipeline Complète

```
Commit → Build → Tests → Coverage → Docker → Tests E2E → Deploy
```

### Vérifier la Couverture Localement

```bash
# Extraire le pourcentage de couverture
mvn test jacoco:report
cat target/site/jacoco/index.html
```

---

## 🐳 Docker

### Construction de l'Image

```bash
# Construire l'image
docker build -t ms-recette:latest .

# Exécuter le conteneur
docker run -p 8081:8081 ms-recette:latest
```

### Docker Compose

```bash
# Démarrer tous les services
docker-compose up -d

# Arrêter les services
docker-compose down

# Voir les logs
docker-compose logs -f ms-recette

# Redémarrer un service
docker-compose restart ms-recette
```

---

## 🚢 Déploiement

### Variables d'Environnement Requises

```env
SERVER_PORT=8081
MS_PERSISTANCE_URL=http://ms-persistance:8090
LOG_LEVEL_ROOT=INFO
USERNAME=VotreNom
USEREMAIL=votre@email.com
```

### Kubernetes

```bash
# Appliquer les manifests
kubectl apply -f k8s/

# Vérifier le déploiement
kubectl get pods
kubectl get services

# Voir les logs
kubectl logs -f deployment/ms-recette
```

### ArgoCD

```bash
# Créer l'application
kubectl apply -f argocd-app.yaml

# Synchroniser
argocd app sync ms-recette
```

---

## 📖 Documentation Complémentaire

- **Architecture détaillée** : `GUIDE-DEVELOPPEUR.md`
- **Guide ArgoCD** : `ARGOCD-QUICKREF.md`

---

## 🤝 Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

---

## 📄 Licence

Ce projet est sous licence MIT.

---

## 👥 Auteurs

- **Abdelmoughit** - abdelbouch2002@gmail.com

---

## 🐛 Troubleshooting

### Problème : Minikube Image Load Error

**Erreur** :
```
X Exiting due to GUEST_IMAGE_LOAD: Failed to load image
blob sha256:... not found
```

**Solution** :
Utiliser `docker save/load` au lieu de `minikube image load` :

```bash
docker pull abdboutchichi/ms-persistance:latest
docker save abdboutchichi/ms-persistance:latest -o /tmp/ms-persistance.tar
eval $(minikube docker-env)
docker load -i /tmp/ms-persistance.tar
```

Voir [FIX-MINIKUBE-IMAGE-LOAD.md](./FIX-MINIKUBE-IMAGE-LOAD.md) pour plus de détails.

### Problème : Coverage < 60%

**Solution** :
```bash
# Vérifier les exclusions JaCoCo dans pom.xml
# Classes exclues : config, request, response, exception, model
mvn clean test jacoco:report
```

### Problème : Tests Newman Échouent

**Solution** :
```bash
# Vérifier que MS-Persistance et MS-Recette sont UP
curl http://localhost:8090/actuator/health
curl http://localhost:8081/actuator/health

# Créer les données de test
cd tests/newman
npm install
npm test
```

---

## 📞 Support

Pour toute question ou problème :
- Ouvrir une issue sur GitHub
- Contacter l'équipe de développement
- Consulter [FIX-MINIKUBE-IMAGE-LOAD.md](./FIX-MINIKUBE-IMAGE-LOAD.md)

---

**🎉 MS-Recette - Microservice de Gestion de Recettes**

