# Tests d'Intégration Newman

Ce dossier contient les tests d'intégration automatisés pour l'API RecipeYouLove.

## 📁 Structure

```
tests/newman/
├── collection.json          # Collection Postman avec tous les endpoints
├── env.json                 # Variables d'environnement (baseUrl, etc.)
├── dataset.json             # Données de test pour itérations
├── index.js                 # Runner Newman personnalisé
├── package.json             # Dépendances Node.js
└── newman-results/          # Rapports générés (créé automatiquement)
```

## 🚀 Utilisation

### Installation

```bash
npm install
```

### Exécution des tests

#### Tests simples (CLI uniquement)
```bash
npm test
```

#### Tests avec rapport HTML
```bash
npm run test:html
```

#### Tests pour CI/CD
```bash
npm run test:ci
```

#### Exécution manuelle avec options
```bash
node index.js \
  --collection ./collection.json \
  --environment ./env.json \
  --data ./dataset.json \
  --reporters cli,html,json
```

## 📊 Dataset - Tests Itératifs

Le fichier `dataset.json` contient des données de test qui permettent d'exécuter **plusieurs itérations** de la collection avec des données différentes.

### Format du Dataset

```json
[
  {
    "recipeName": "Pasta Carbonara",
    "ingredients": ["pasta", "eggs", "bacon", "parmesan"],
    "difficulty": "easy"
  },
  {
    "recipeName": "Chocolate Cake",
    "ingredients": ["flour", "chocolate", "sugar", "eggs"],
    "difficulty": "medium"
  }
]
```

### Comment ça marche ?

1. **POST** - Crée une nouvelle recette avec les données de l'itération
2. **GET** - Récupère la recette créée
3. **PUT** - Met à jour la recette
4. **DELETE** - Supprime la recette

Chaque entrée dans le dataset génère une **itération complète** de tous ces tests.

## 🧪 Tests Couverts

### Endpoints testés

- `POST /api/recipes` - Création de recettes
- `GET /api/recipes` - Liste des recettes
- `GET /api/recipes/{id}` - Détail d'une recette
- `PUT /api/recipes/{id}` - Mise à jour d'une recette
- `DELETE /api/recipes/{id}` - Suppression d'une recette

### Assertions

- ✅ Status codes (200, 201, 204, 404, etc.)
- ✅ Response time < 2000ms
- ✅ Content-Type headers
- ✅ Response body structure
- ✅ Data validation

## 📝 Variables d'Environnement

Le fichier `env.json` contient les variables utilisées dans les tests :

```json
{
  "name": "Local",
  "values": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080",
      "enabled": true
    }
  ]
}
```

Ces variables sont automatiquement remplacées dans la pipeline CI/CD par l'URL du service déployé.

## 📈 Rapports

Après l'exécution, les rapports sont générés dans `newman-results/` :

- `newman-report.html` - Rapport visuel complet
- `newman-report.json` - Données brutes pour analyse

## 🔧 Configuration Newman

Le script `index.js` supporte les options suivantes :

| Option | Alias | Défaut | Description |
|--------|-------|--------|-------------|
| `--collection` | `-c` | `./collection.json` | Fichier de collection |
| `--environment` | `-e`, `--env` | `./env.json` | Fichier d'environnement |
| `--data` | `-d` | `./dataset.json` | Dataset pour itérations |
| `--reporters` | - | `cli,json,html` | Reporters à utiliser |
| `--output` | - | `./newman-results` | Dossier de sortie |

## 🎯 Intégration CI/CD

Ces tests sont automatiquement exécutés dans la pipeline GitHub Actions après le déploiement :

```yaml
- name: Run Newman integration tests
  run: |
    SERVICE_URL=$(cat service-url.txt)
    jq --arg url "$SERVICE_URL" '.values[0].value = $url' env.json > env.tmp.json
    node index.js --collection ./collection.json --environment ./env.tmp.json --data ./dataset.json
```

## 💡 Exemples

### Ajouter une nouvelle itération au dataset

Éditez `dataset.json` :

```json
[
  {
    "recipeName": "New Recipe",
    "ingredients": ["item1", "item2"],
    "difficulty": "easy"
  }
]
```

### Modifier l'URL de test

Éditez `env.json` :

```json
{
  "values": [
    {
      "key": "baseUrl",
      "value": "http://your-service-url:8080"
    }
  ]
}
```

### Exécuter contre un environnement spécifique

```bash
node index.js \
  --collection ./collection.json \
  --environment ./env-production.json \
  --data ./dataset.json
```

## 🐛 Dépannage

### Tests échouent avec "Connection refused"

- Vérifiez que le service est démarré
- Vérifiez l'URL dans `env.json`
- Testez manuellement : `curl http://localhost:8080/actuator/health`

### Erreur "Cannot find module 'newman'"

```bash
npm install
```

### Dataset non chargé

- Vérifiez que `dataset.json` est bien formaté (JSON valide)
- Vérifiez le chemin du fichier

## 📚 Ressources

- [Newman Documentation](https://github.com/postmanlabs/newman)
- [Postman Collections](https://learning.postman.com/docs/collections/collections-overview/)
- [Data-driven Testing](https://learning.postman.com/docs/running-collections/working-with-data-files/)

