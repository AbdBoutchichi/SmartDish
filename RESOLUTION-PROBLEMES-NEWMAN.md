# 🔧 Résolution des Problèmes - Tests Newman

## ✅ Problème Résolu : 404 dans les Tests

### 🔍 Cause
La collection Newman testait des endpoints `/api/items/*` qui **n'existent pas** dans votre application.

### ✅ Solution Appliquée
**Modification de la collection Newman** pour tester les vrais endpoints :

#### Endpoints Disponibles dans Votre Application
```
✅ /                        → Home page
✅ /health                  → Health check
✅ /api/status              → Application status
✅ /api/database/test       → Test connexion MySQL
✅ /actuator/health         → Actuator health endpoint
```

## 📝 Fichiers Modifiés

### 1. `tests/newman/collection.json`
- ❌ Supprimé : Tests CRUD sur `/api/items` (inexistants)
- ✅ Ajouté : Tests des 5 endpoints réels de l'application
- ✅ Ajouté : Assertions pour vérifier les réponses

### 2. `tests/newman/dataset.json`
- ✅ Simplifié : Plus besoin de données d'items

### 3. `test-newman-local.ps1` (nouveau)
- ✅ Script pour tester localement avant de pousser

## 🧪 Tests Locaux

### Démarrer l'Application
```bash
mvn spring-boot:run
```

### Tester Manuellement les Endpoints
```powershell
# Home
curl http://localhost:8080/

# Health
curl http://localhost:8080/health

# Status
curl http://localhost:8080/api/status

# Database
curl http://localhost:8080/api/database/test

# Actuator
curl http://localhost:8080/actuator/health
```

### Exécuter les Tests Newman
```powershell
.\test-newman-local.ps1
```

## 🌐 Problème d'Accès depuis le Navigateur

Si vous avez des problèmes d'accès aux URLs depuis le navigateur, plusieurs causes possibles :

### 1. Application Non Démarrée
```bash
# Vérifier si l'app tourne
curl http://localhost:8080/health

# Si erreur, démarrer l'app
mvn spring-boot:run
```

### 2. Port Déjà Utilisé
```powershell
# Vérifier quel processus utilise le port 8080
netstat -ano | findstr :8080

# Tuer le processus si nécessaire
taskkill /PID <PID> /F
```

### 3. Firewall Bloquant
```powershell
# Autoriser temporairement (en tant qu'admin)
New-NetFirewallRule -DisplayName "Spring Boot Dev" -Direction Inbound -LocalPort 8080 -Protocol TCP -Action Allow
```

### 4. Problème de Context Path
Vérifier dans `application.properties` :
```properties
# S'assurer qu'il n'y a pas de context path personnalisé
# server.servlet.context-path=/custom-path
```

### 5. Dans GitHub Actions (Minikube)
Les URLs Minikube (192.168.49.2:XXXXX) **ne sont accessibles QUE depuis le runner GitHub Actions**, pas depuis votre navigateur local.

Pour accéder localement à Minikube :
```bash
# Depuis votre machine locale où Minikube tourne
minikube service univ-soa -n soa-integration

# Ou port-forward
kubectl port-forward svc/univ-soa 8080:8080 -n soa-integration
# Puis accéder à http://localhost:8080
```

## 🔍 Debugging des Erreurs d'Accès

### Vérifier que l'Application Répond
```powershell
# Test simple
Invoke-WebRequest -Uri "http://localhost:8080/health" -UseBasicParsing

# Avec détails
curl -v http://localhost:8080/health
```

### Vérifier les Logs de l'Application
```bash
# En développement local
mvn spring-boot:run
# Les logs s'affichent dans la console

# Si lancé avec java -jar
tail -f logs/application.log
```

### Vérifier dans le Navigateur
1. Ouvrir la console développeur (F12)
2. Aller dans l'onglet "Réseau"
3. Faire la requête
4. Vérifier :
   - Le statut HTTP (200, 404, 500, etc.)
   - Les headers de réponse
   - Le corps de la réponse

## 🚀 Workflow GitHub Actions

Avec les modifications, le workflow devrait maintenant :

1. ✅ Déployer l'application dans Minikube
2. ✅ Tester la santé du service
3. ✅ Exécuter Newman avec les bons endpoints
4. ✅ Tous les tests passent (plus de 404)

## 📊 Résultats Attendus

```
newman

RecipeYouLove API Tests

→ Health Check
  GET http://192.168.49.2:XXXXX/health [200 OK]
  ✓ Status code is 200
  ✓ Response contains health message

→ API Status
  GET http://192.168.49.2:XXXXX/api/status [200 OK]
  ✓ Status code is 200
  ✓ Response has JSON body
  ✓ Response contains application name

→ Database Connection Test
  GET http://192.168.49.2:XXXXX/api/database/test [200 OK]
  ✓ Status code is 200
  ✓ Database connection is successful

→ Home Page
  GET http://192.168.49.2:XXXXX/ [200 OK]
  ✓ Status code is 200
  ✓ Response contains API running message

→ Actuator Health
  GET http://192.168.49.2:XXXXX/actuator/health [200 OK]
  ✓ Status code is 200
  ✓ Health status is UP

┌─────────────────────────┬───────────────────┬──────────────────┐
│                         │          executed │           failed │
├─────────────────────────┼───────────────────┼──────────────────┤
│              iterations │                 1 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│                requests │                 5 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│            test-scripts │                 5 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│      prerequest-scripts │                 0 │                0 │
├─────────────────────────┼───────────────────┼──────────────────┤
│              assertions │                11 │                0 │
└─────────────────────────┴───────────────────┴──────────────────┘

✅ All Newman tests passed successfully!
```

## ✨ Commandes Rapides

```bash
# 1. Tester localement
mvn spring-boot:run
# Dans un autre terminal :
.\test-newman-local.ps1

# 2. Commit et push
git add .
git commit -m "fix: collection Newman mise à jour avec les vrais endpoints"
git push

# 3. Vérifier dans GitHub Actions
# Les tests devraient maintenant passer sans 404
```

## 🎯 Points Clés

1. **Collection Newman = Endpoints Réels**
   - Ne testez que ce qui existe vraiment dans votre code
   
2. **Tests Locaux Avant Push**
   - Toujours tester avec `test-newman-local.ps1`
   
3. **URLs Minikube ≠ URLs Locales**
   - Minikube : `http://192.168.49.2:XXXXX` (GitHub Actions uniquement)
   - Local : `http://localhost:8080` (votre machine)

4. **Pas de 404 = Endpoints Corrects**
   - Les tests passent maintenant car ils testent les bons endpoints

