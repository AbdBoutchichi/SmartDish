# 🗄️ ms-persistance - Microservice Persistance

## 📖 Vue d'ensemble

Le **microservice Persistance** est le service central de gestion des données pour l'application **SmartDish**. Il centralise tous les accès à la base de données MySQL et expose une API REST pour les autres microservices.

### Responsabilités

- 🗄️ Gestion centralisée des données MySQL
- 🔐 Validation des données et règles métier
- 🔗 Gestion des relations entre entités
- 📊 CRUD complet (Create, Read, Update, Delete)
- ✅ Intégrité référentielle

## 🏗️ Architecture

```
┌──────────────┐                    ┌──────────────┐
│ ms-feedback  │───┐                │              │
└──────────────┘   │                │              │
                   │   HTTP REST    │              │
┌──────────────┐   ├───────────────>│ms-persistance│
│ ms-recette   │───┤                │  (Port 8090) │
└──────────────┘   │                │              │
                   │                │              │
┌──────────────┐   │                │              │
│ms-utilisateur│───┘                │              │
└──────────────┘                    └──────┬───────┘
                                           │
                                           ▼
                                    ┌──────────────┐
                                    │    MySQL     │
                                    │  (Port 3307) │
                                    └──────────────┘
```

### Stack Technologique

- **Framework** : Spring Boot 3.5.6
- **Langage** : Java 21
- **Base de données** : MySQL 8.0
- **ORM** : JPA / Hibernate
- **Build** : Maven 3.8+
- **Documentation** : Swagger/OpenAPI

## 🚀 Installation

### Démarrage

#### 1. Cloner le projet

```bash
git clone https://github.com/Sabine22-alt/ms-persistance.git
cd ms-persistance
```

#### 2. Configurer l'environnement

Récupérer le fichier `.env` auprès de l'administrateur et le placer à la racine du projet.

#### 3. Première exécution - Créer les tables

```bash
# Modifier .env : JPA_DDL_AUTO=create
mvn spring-boot:run

# ✅ Les 7 tables sont créées automatiquement
```

#### 5. Exécutions suivantes - Mode update

```bash
# Modifier .env : JPA_DDL_AUTO=update
mvn spring-boot:run
```

## 🔗 Accès aux services

| Service | URL |
|---------|-----|
| **Swagger UI** | http://localhost:8090/swagger-ui.html |
| **phpMyAdmin** | http://localhost:8080 |

## 📡 API Endpoints

### Utilisateurs

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/persistance/utilisateurs` | Liste tous les utilisateurs |
| `GET` | `/api/persistance/utilisateurs/{id}` | Obtenir un utilisateur |
| `POST` | `/api/persistance/utilisateurs` | Créer un utilisateur |
| `PUT` | `/api/persistance/utilisateurs/{id}` | Mettre à jour un utilisateur |
| `DELETE` | `/api/persistance/utilisateurs/{id}` | Supprimer un utilisateur |

### Aliments

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/persistance/aliments` | Liste tous les aliments |
| `GET` | `/api/persistance/aliments/{id}` | Obtenir un aliment |
| `POST` | `/api/persistance/aliments` | Créer un aliment |
| `PUT` | `/api/persistance/aliments/{id}` | Mettre à jour un aliment |
| `DELETE` | `/api/persistance/aliments/{id}` | Supprimer un aliment |

### Recettes

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/persistance/recettes` | Liste toutes les recettes |
| `GET` | `/api/persistance/recettes/{id}` | Obtenir une recette |
| `POST` | `/api/persistance/recettes` | Créer une recette |
| `PUT` | `/api/persistance/recettes/{id}` | Mettre à jour une recette |
| `DELETE` | `/api/persistance/recettes/{id}` | Supprimer une recette |

### Feedbacks

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/persistance/feedbacks` | Liste tous les feedbacks |
| `GET` | `/api/persistance/feedbacks/{id}` | Obtenir un feedback |
| `GET` | `/api/persistance/feedbacks/utilisateur/{id}` | Feedbacks d'un utilisateur |
| `GET` | `/api/persistance/feedbacks/recette/{id}` | Feedbacks d'une recette |
| `POST` | `/api/persistance/feedbacks` | Créer un feedback |
| `PUT` | `/api/persistance/feedbacks/{id}` | Mettre à jour un feedback |
| `DELETE` | `/api/persistance/feedbacks/{id}` | Supprimer un feedback |

## 🗂️ Structure du projet

```
ms-persistance/
├── src/main/java/.../
│   ├── config/
│   ├── controller/
│   │   ├── UtilisateurController.java
│   │   ├── AlimentController.java
│   │   ├── RecetteController.java
│   │   └── FeedbackController.java
│   ├── dto/
│   ├── exception/
│   ├── mapper/
│   ├── model/
│   │   ├── Utilisateur.java
│   │   ├── Aliment.java
│   │   ├── Recette.java
│   │   ├── Ingredient.java
│   │   ├── Etape.java
│   │   └── Feedback.java
│   ├── repository/
│   └── service/
├── .env                 # Fourni par l'admin (non versionné)
└── pom.xml
```

## 📊 Base de données

### 7 Tables créées automatiquement

1. **utilisateurs** - Comptes utilisateurs
2. **aliments** - Catalogue d'aliments
3. **recettes** - Recettes de cuisine
4. **ingredients** - Ingrédients des recettes (liaison)
5. **etapes** - Étapes de préparation
6. **feedbacks** - Notes et commentaires
7. **aliments_exclus** - Aliments exclus par utilisateur (liaison)

### Types d'énumérations

- **Role** : `USER`, `ADMIN`
- **CategorieAliment** : `FRUIT`, `LEGUME`, `VIANDE`, `POISSON`, `CEREALE`, `LAITIER`, `EPICE`, `GLUTEN`
- **Difficulte** : `FACILE`, `MOYEN`, `DIFFICILE`
- **Unite** : `GRAMME`, `KILOGRAMME`, `LITRE`, `MILLILITRE`, `CUILLERE_A_SOUPE`, `CUILLERE_A_CAFE`, `SACHET`, `UNITE`

## 🛡️ Validations implémentées

### Utilisateurs
- Email unique et format valide
- Mot de passe min 6 caractères (hashé BCrypt)
- Nom et prénom obligatoires

### Aliments
- Nom unique (2-100 caractères)
- Catégorie obligatoire

### Recettes
- Titre obligatoire (3-200 caractères)
- Temps total > 0 et ≤ 1440 minutes
- Calories ≥ 0 et ≤ 10000

### Feedbacks
- Utilisateur et recette doivent exister
- Évaluation entre 1 et 5
- **Un utilisateur ne peut noter qu'une fois une recette**

---

## 🔄 Pour les autres microservices

### Si votre microservice accède directement à MySQL

Vous devez migrer vers l'architecture HTTP. Voici les étapes :

#### 1. Créer un client HTTP (exemple)

```java
@Component
public class PersistanceClient {
    private final RestTemplate restTemplate;
    
    @Value("${persistance.service.url}")
    private String persistanceServiceUrl;

    // Récupérer toutes les recettes
    public List<RecetteDTO> getAllRecettes() {
        String url = persistanceServiceUrl + "/api/persistance/recettes";
        ResponseEntity<List<RecetteDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<RecetteDTO>>() {}
        );
        return response.getBody();
    }
}
```

#### 2. Mettre à jour application.properties

```properties
# Retirer la configuration MySQL directe
spring.autoconfigure.exclude=\
  org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
  org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration

# Ajouter l'URL du service Persistance
persistance.service.url=${PERSISTANCE_SERVICE_URL}
```

#### 3. Mettre à jour pom.xml

```xml
<!-- Retirer -->
<!-- <dependency>spring-boot-starter-data-jpa</dependency> -->
<!-- <dependency>mysql-connector-j</dependency> -->

<!-- Garder -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

#### 4. Mettre à jour .env

```env
# Retirer : MYSQL_*, JPA_*
# Ajouter :
PERSISTANCE_SERVICE_URL=http://localhost:8090
```

### 📦 Exemple complet

Voir le microservice **[ms-feedback](https://github.com/nassimug/ms-feedback)** comme référence d'une migration réussie.

---

## 🚀 Build production

```bash
# Créer le JAR
mvn clean package -DskipTests

# Lancer
java -jar target/ms-persistance-1.0.0.jar
```

## 📚 Ressources

- [Documentation Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Swagger/OpenAPI](https://swagger.io/docs/)
- [Exemple ms-feedback](https://github.com/nassimug/ms-feedback)

---
