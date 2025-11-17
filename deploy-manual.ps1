# Script de déploiement manuel sur Kubernetes OKE
# Usage: .\deploy-manual.ps1 -Environment integration -ImageTag latest

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("integration", "production")]
    [string]$Environment = "integration",

    [Parameter(Mandatory=$false)]
    [string]$ImageTag = "latest",

    [Parameter(Mandatory=$false)]
    [string]$MicroserviceName = "univ.soa"
)

# Configuration
$NAMESPACE = if ($Environment -eq "production") { "production" } else { "integration" }
$REPLICAS = if ($Environment -eq "production") { 2 } else { 1 }
$SPRING_PROFILE = if ($Environment -eq "production") { "production" } else { "integration" }
$LOG_LEVEL = if ($Environment -eq "production") { "INFO" } else { "DEBUG" }
$LOG_LEVEL_JDBC = if ($Environment -eq "production") { "WARN" } else { "DEBUG" }

Write-Host "=======================================" -ForegroundColor Cyan
Write-Host "DÉPLOIEMENT MANUEL KUBERNETES" -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host "Environnement: $Environment" -ForegroundColor Yellow
Write-Host "Namespace: $NAMESPACE" -ForegroundColor Yellow
Write-Host "Microservice: $MicroserviceName" -ForegroundColor Yellow
Write-Host "Image Tag: $ImageTag" -ForegroundColor Yellow
Write-Host "Replicas: $REPLICAS" -ForegroundColor Yellow
Write-Host ""

# Vérifier la connexion au cluster
Write-Host "1. Vérification de la connexion au cluster..." -ForegroundColor Cyan
try {
    kubectl cluster-info | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "Impossible de se connecter au cluster"
    }
    Write-Host "   ✅ Connexion au cluster OK" -ForegroundColor Green
    kubectl get nodes
} catch {
    Write-Host "   ❌ ERREUR: Impossible de se connecter au cluster Kubernetes" -ForegroundColor Red
    Write-Host "   Vérifiez votre kubeconfig dans ~/.kube/config" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Créer le namespace
Write-Host "2. Création du namespace..." -ForegroundColor Cyan
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -
kubectl label namespace $NAMESPACE environment=$Environment --overwrite
Write-Host "   ✅ Namespace $NAMESPACE créé/mis à jour" -ForegroundColor Green

Write-Host ""

# Demander les secrets si nécessaires
Write-Host "3. Configuration des secrets..." -ForegroundColor Cyan

# Secret OCIR
Write-Host "   Vérification du secret OCIR..." -ForegroundColor Yellow
$ocirSecretExists = kubectl get secret ocir-secret -n $NAMESPACE 2>&1 | Out-String
if ($ocirSecretExists -match "NotFound" -or $ocirSecretExists -match "not found") {
    Write-Host "   ⚠️  Secret OCIR non trouvé. Création nécessaire..." -ForegroundColor Yellow

    $OCI_TENANCY_NAMESPACE = Read-Host "   Entrez OCI_TENANCY_NAMESPACE (ex: axfuowvuxal7)"
    $OCI_USERNAME = Read-Host "   Entrez OCI_USERNAME (ex: abdelmoughitboutchi4@gmail.com)"
    $OCI_AUTH_TOKEN = Read-Host "   Entrez OCI_AUTH_TOKEN" -AsSecureString
    $OCI_AUTH_TOKEN_PLAIN = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($OCI_AUTH_TOKEN))

    # Construire le username complet
    if ($OCI_USERNAME -notmatch "/") {
        $FULL_USERNAME = "${OCI_TENANCY_NAMESPACE}/${OCI_USERNAME}"
    } else {
        $FULL_USERNAME = $OCI_USERNAME
    }

    kubectl create secret docker-registry ocir-secret `
        --docker-server=cdg.ocir.io `
        --docker-username="$FULL_USERNAME" `
        --docker-password="$OCI_AUTH_TOKEN_PLAIN" `
        --namespace=$NAMESPACE

    Write-Host "   ✅ Secret OCIR créé" -ForegroundColor Green
} else {
    Write-Host "   ✅ Secret OCIR existe déjà" -ForegroundColor Green
}

# Secret MySQL
Write-Host "   Vérification des secrets MySQL..." -ForegroundColor Yellow
$mysqlSecretExists = kubectl get secret mysql-secrets -n $NAMESPACE 2>&1 | Out-String
if ($mysqlSecretExists -match "NotFound" -or $mysqlSecretExists -match "not found") {
    Write-Host "   ⚠️  Secrets MySQL non trouvés. Création nécessaire..." -ForegroundColor Yellow

    $MYSQL_HOST = Read-Host "   Entrez MYSQL_HOST"
    $MYSQL_USER = Read-Host "   Entrez MYSQL_USER"
    $MYSQL_PASSWORD = Read-Host "   Entrez MYSQL_PASSWORD" -AsSecureString
    $MYSQL_PASSWORD_PLAIN = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($MYSQL_PASSWORD))
    $MYSQL_ROOT_PASSWORD = Read-Host "   Entrez MYSQL_ROOT_PASSWORD" -AsSecureString
    $MYSQL_ROOT_PASSWORD_PLAIN = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($MYSQL_ROOT_PASSWORD))

    kubectl create secret generic mysql-secrets `
        --from-literal=MYSQL_HOST="$MYSQL_HOST" `
        --from-literal=MYSQL_PORT="3306" `
        --from-literal=MYSQL_DATABASE="smartdish" `
        --from-literal=MYSQL_USER="$MYSQL_USER" `
        --from-literal=MYSQL_PASSWORD="$MYSQL_PASSWORD_PLAIN" `
        --from-literal=MYSQL_ROOT_PASSWORD="$MYSQL_ROOT_PASSWORD_PLAIN" `
        --namespace=$NAMESPACE

    Write-Host "   ✅ Secrets MySQL créés" -ForegroundColor Green
} else {
    Write-Host "   ✅ Secrets MySQL existent déjà" -ForegroundColor Green
}

Write-Host ""

# Créer la ConfigMap
Write-Host "4. Création de la ConfigMap..." -ForegroundColor Cyan
kubectl create configmap app-config `
    --from-literal=SPRING_PROFILES_ACTIVE=$SPRING_PROFILE `
    --from-literal=LOG_LEVEL=$LOG_LEVEL `
    --from-literal=LOG_LEVEL_JDBC=$LOG_LEVEL_JDBC `
    --namespace=$NAMESPACE `
    --dry-run=client -o yaml | kubectl apply -f -
Write-Host "   ✅ ConfigMap créée" -ForegroundColor Green

Write-Host ""

# Demander le tenancy namespace pour l'image
Write-Host "5. Préparation du déploiement..." -ForegroundColor Cyan
if (-not $OCI_TENANCY_NAMESPACE) {
    $OCI_TENANCY_NAMESPACE = Read-Host "   Entrez OCI_TENANCY_NAMESPACE (ex: axfuowvuxal7)"
}

$IMAGE_REPO = "cdg.ocir.io/${OCI_TENANCY_NAMESPACE}/smartdish/${MicroserviceName}"
$IMAGE_FULL = "${IMAGE_REPO}:${ImageTag}"

Write-Host "   Image à déployer: $IMAGE_FULL" -ForegroundColor Yellow

# Générer le fichier de déploiement
$deploymentTemplate = Get-Content "k8s/oci/deployment-template.yaml" -Raw
$deploymentYaml = $deploymentTemplate `
    -replace '\$\{MICROSERVICE_NAME\}', $MicroserviceName `
    -replace '\$\{NAMESPACE\}', $NAMESPACE `
    -replace '\$\{VERSION\}', $ImageTag `
    -replace '\$\{REPLICAS\}', $REPLICAS `
    -replace '\$\{IMAGE_URL\}', $IMAGE_REPO `
    -replace '\$\{IMAGE_TAG\}', $ImageTag

# Sauvegarder le fichier généré
$deploymentYaml | Out-File -FilePath "k8s/oci/deployment-generated.yaml" -Encoding UTF8
Write-Host "   ✅ Fichier de déploiement généré: k8s/oci/deployment-generated.yaml" -ForegroundColor Green

Write-Host ""

# Appliquer le déploiement
Write-Host "6. Application du déploiement..." -ForegroundColor Cyan
kubectl apply -f k8s/oci/deployment-generated.yaml
if ($LASTEXITCODE -ne 0) {
    Write-Host "   ❌ ERREUR lors de l'application du déploiement" -ForegroundColor Red
    exit 1
}
Write-Host "   ✅ Déploiement appliqué" -ForegroundColor Green

Write-Host ""

# Vérifier le déploiement
Write-Host "7. Vérification du déploiement..." -ForegroundColor Cyan
Write-Host "   Attente du rollout..." -ForegroundColor Yellow
kubectl rollout status deployment/$MicroserviceName -n $NAMESPACE --timeout=5m

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "=======================================" -ForegroundColor Green
    Write-Host "✅ DÉPLOIEMENT RÉUSSI !" -ForegroundColor Green
    Write-Host "=======================================" -ForegroundColor Green
    Write-Host ""

    Write-Host "Pods:" -ForegroundColor Cyan
    kubectl get pods -n $NAMESPACE -l app=$MicroserviceName

    Write-Host ""
    Write-Host "Services:" -ForegroundColor Cyan
    kubectl get svc -n $NAMESPACE -l app=$MicroserviceName

    Write-Host ""
    Write-Host "Pour voir les logs:" -ForegroundColor Yellow
    Write-Host "  kubectl logs -n $NAMESPACE -l app=$MicroserviceName --tail=100 -f" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "=======================================" -ForegroundColor Red
    Write-Host "❌ ÉCHEC DU DÉPLOIEMENT" -ForegroundColor Red
    Write-Host "=======================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Logs des pods:" -ForegroundColor Yellow
    kubectl get pods -n $NAMESPACE -l app=$MicroserviceName
    Write-Host ""
    kubectl describe pods -n $NAMESPACE -l app=$MicroserviceName
    exit 1
}

